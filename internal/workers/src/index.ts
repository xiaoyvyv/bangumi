/**
 * Bangumi API Proxy - Cloudflare Workers
 *
 * 代理所有 Bangumi 相关域名的请求
 * 使用方式: GET/POST/PATCH/PUT/DELETE {proxy_host}/{target_url}
 * 例如: GET bangumi-proxy.pages.dev/https://api.bgm.tv/v0/subjects/1
 */

export interface Env {
  ALLOWED_HOSTS: string;
}

/** 允许代理的 Bangumi 域名 */
const DEFAULT_ALLOWED_HOSTS = new Set([
  "api.bgm.tv",
  "bgm.tv",
  "lain.bgm.tv",
  "bangumi.tv",
  "chii.in",
  "next.bgm.tv",
]);

/** 不转发给目标的请求头 */
const HOP_BY_HOP_HEADERS = new Set([
  "connection",
  "keep-alive",
  "proxy-authenticate",
  "proxy-authorization",
  "te",
  "trailers",
  "transfer-encoding",
  "upgrade",
  "host",
  "cf-connecting-ip",
  "cf-ipcountry",
  "cf-ray",
  "cf-visitor",
  "x-forwarded-for",
  "x-forwarded-proto",
  "x-real-ip",
]);

/** 从响应中移除的头 */
const RESPONSE_HEADERS_TO_REMOVE = new Set([
  "connection",
  "keep-alive",
  "transfer-encoding",
  "set-cookie",
]);

function getAllowedHosts(env: Env): Set<string> {
  if (env.ALLOWED_HOSTS) {
    return new Set(env.ALLOWED_HOSTS.split(",").map((h) => h.trim()));
  }
  return DEFAULT_ALLOWED_HOSTS;
}

function buildCorsHeaders(origin: string | null): Record<string, string> {
  const base: Record<string, string> = {
    "Access-Control-Allow-Methods":
      "GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS",
    "Access-Control-Allow-Headers":
      "Content-Type, Authorization, User-Agent, Accept, X-Requested-With",
    "Access-Control-Expose-Headers": "*",
    "Access-Control-Max-Age": "86400",
    Vary: "Origin",
  };

  if (origin) {
    return {
      ...base,
      "Access-Control-Allow-Origin": origin,
      "Access-Control-Allow-Credentials": "true",
    };
  }

  return {
    ...base,
    "Access-Control-Allow-Origin": "*",
  };
}

function extractTargetUrl(request: Request): string | null {
  const url = new URL(request.url);
  // 路径格式: /https://api.bgm.tv/v0/subjects/1
  // pathname 会是 /https://api.bgm.tv/v0/subjects/1
  // 我们需要去掉开头的 /
  let targetPath = url.pathname.slice(1) + url.search;

  if (!targetPath) {
    return null;
  }

  // 处理 URL 编码的情况
  if (targetPath.startsWith("http%3A") || targetPath.startsWith("https%3A")) {
    targetPath = decodeURIComponent(targetPath);
  }

  // 验证是否是有效的 URL
  if (!targetPath.startsWith("http://") && !targetPath.startsWith("https://")) {
    return null;
  }

  return targetPath;
}

function buildProxiedHeaders(
  request: Request,
  targetUrl: URL
): Headers {
  const headers = new Headers();

  for (const [key, value] of request.headers.entries()) {
    if (!HOP_BY_HOP_HEADERS.has(key.toLowerCase())) {
      headers.set(key, value);
    }
  }

  // 设置正确的 Host
  headers.set("Host", targetUrl.host);

  return headers;
}

function buildResponseHeaders(
  response: Response,
  corsHeaders: Record<string, string>
): Headers {
  const headers = new Headers();

  for (const [key, value] of response.headers.entries()) {
    if (!RESPONSE_HEADERS_TO_REMOVE.has(key.toLowerCase())) {
      headers.set(key, value);
    }
  }

  // 添加 CORS 头
  for (const [key, value] of Object.entries(corsHeaders)) {
    headers.set(key, value);
  }

  return headers;
}

function getSetCookieHeaders(response: Response): string[] {
  const headersAny = response.headers as unknown as {
    getSetCookie?: () => string[];
  };

  if (typeof headersAny.getSetCookie === "function") {
    return headersAny.getSetCookie();
  }

  const single = response.headers.get("set-cookie");
  return single ? [single] : [];
}

function rewriteSetCookie(cookie: string, proxyHostname: string): string {
  const parts = cookie.split(";").map((p) => p.trim()).filter(Boolean);
  if (parts.length === 0) {
    return cookie;
  }

  const nameValue = parts[0];
  const name = nameValue.split("=", 1)[0]?.trim() ?? "";
  const isHostPrefix = name.startsWith("__Host-");

  const attrs = parts
    .slice(1)
    .filter((attr) => !/^domain=/i.test(attr));

  if (isHostPrefix) {
    return [nameValue, ...attrs].join("; ");
  }

  const cookieDomain = proxyHostname.startsWith(".")
    ? proxyHostname
    : `.${proxyHostname}`;

  return [nameValue, `Domain=${cookieDomain}`, ...attrs].join("; ");
}

function rewriteLocation(
  location: string,
  proxyOrigin: string,
  targetBaseUrl: URL,
  allowedHosts: Set<string>
): string {
  let resolved: URL;
  try {
    resolved = new URL(location, targetBaseUrl);
  } catch {
    return location;
  }

  if (!allowedHosts.has(resolved.hostname)) {
    return location;
  }

  return `${proxyOrigin}/${resolved.toString()}`;
}

function jsonResponse(
  data: Record<string, unknown>,
  status: number,
  corsHeaders: Record<string, string>
): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      "Content-Type": "application/json",
      ...corsHeaders,
    },
  });
}

export default {
  async fetch(
    request: Request,
    env: Env,
    _ctx: ExecutionContext
  ): Promise<Response> {
    const origin = request.headers.get("Origin");
    const corsHeaders = buildCorsHeaders(origin);

    // 处理 CORS 预检请求
    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders });
    }

    // 根路径返回使用说明
    const url = new URL(request.url);
    if (url.pathname === "/" || url.pathname === "") {
      return jsonResponse(
        {
          name: "Bangumi API Proxy",
          usage:
            "/{target_url} - 例如 /https://api.bgm.tv/v0/subjects/1",
          allowed_hosts: Array.from(getAllowedHosts(env)),
          methods: ["GET", "POST", "PUT", "PATCH", "DELETE", "HEAD"],
        },
        200,
        corsHeaders
      );
    }

    // 提取目标 URL
    const targetUrlStr = extractTargetUrl(request);
    if (!targetUrlStr) {
      return jsonResponse(
        {
          error: "Invalid target URL",
          usage:
            "/{target_url} - 例如 /https://api.bgm.tv/v0/subjects/1",
        },
        400,
        corsHeaders
      );
    }

    // 解析目标 URL
    let targetUrl: URL;
    try {
      targetUrl = new URL(targetUrlStr);
    } catch {
      return jsonResponse(
        { error: "Invalid target URL format" },
        400,
        corsHeaders
      );
    }

    // 验证目标域名是否在允许列表中
    const allowedHosts = getAllowedHosts(env);
    if (!allowedHosts.has(targetUrl.hostname)) {
      return jsonResponse(
        {
          error: `Host "${targetUrl.hostname}" is not allowed`,
          allowed_hosts: Array.from(allowedHosts),
        },
        403,
        corsHeaders
      );
    }

    // 构建代理请求
    const proxyHeaders = buildProxiedHeaders(request, targetUrl);
    const proxyInit: RequestInit = {
      method: request.method,
      headers: proxyHeaders,
      redirect: "manual",
    };

    // 对于有 body 的请求方法，转发 body
    if (!["GET", "HEAD"].includes(request.method)) {
      proxyInit.body = request.body;
    }

    try {
      const response = await fetch(targetUrl.toString(), proxyInit);
      const responseHeaders = buildResponseHeaders(response, corsHeaders);
      const setCookies = getSetCookieHeaders(response);
      const proxyHostname = new URL(request.url).hostname;
      for (const cookie of setCookies) {
        responseHeaders.append("Set-Cookie", rewriteSetCookie(cookie, proxyHostname));
      }

      const location = response.headers.get("location");
      if (location) {
        const proxyOrigin = new URL(request.url).origin;
        responseHeaders.set(
          "location",
          rewriteLocation(location, proxyOrigin, targetUrl, allowedHosts)
        );
      }

      return new Response(response.body, {
        status: response.status,
        statusText: response.statusText,
        headers: responseHeaders,
      });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Unknown error";
      return jsonResponse(
        { error: "Proxy request failed", detail: message },
        502,
        corsHeaders
      );
    }
  },
};
