// ==UserScript==
// @name         Bangumi 登录持久化插件
// @namespace    xiaoyvyv
// @version      1.0.0
// @description  登录持久化插件
// @author       1223414335@qq.com
// @match        https://bgm.tv/settings/privacy
// @match        https://bangumi.tv/settings/privacy
// @match        https://chii.in/settings/privacy
// @grant        none
// ==/UserScript==

function init() {
    console.log("Bangumi 登录持久化插件")
    window.endurance = {
        setCookie: function setCookie(name, value, domain, daysToExpire) {
            const expirationDate = new Date();
            expirationDate.setDate(expirationDate.getDate() + daysToExpire);
            const cookie = encodeURIComponent(decodeURIComponent(value));
            document.cookie = `${encodeURIComponent(name)}=${cookie}; expires=${expirationDate.toUTCString()}; domain=${domain}; path=/`;
        },
        getCookie: function getCookie(name) {
            const cookieString = document.cookie;
            const cookies = cookieString.split('; ');
            for (const cookie of cookies) {
                const [cookieName, cookieValue] = cookie.split('=');
                if (cookieName === name) {
                    return decodeURIComponent(cookieValue.trim()).trim();
                }
            }
            return '';
        },
        removeCookie: function removeCookie(name, domain) {
            document.cookie = `${encodeURIComponent(name)}=; domain=${domain}; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
        }
    }

    const columnA = document.querySelector("#columnA");
    if (columnA != null) {
        const insertHtml =
            "<form name=\"endurance\" id=\"endurance\">\n" +
            "    <table align=\"center\" width=\"98%\" cellspacing=\"0\" cellpadding=\"5\" class=\"settings\">\n" +
            "        <tbody>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" width=\"12%\"><h2 class=\"subtitle\">登录持久化</h2></td>\n" +
            "            <td valign=\"top\"></td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" width=\"25%\">持久化登录信息</td>\n" +
            "            <td valign=\"top\">\n" +
            "                <select name=\"endurance\">\n" +
            "                    <option value=\"0\">不持久化</option>\n" +
            "                    <option value=\"1\">持久化</option>\n" +
            "                </select>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" width=\"12%\">\n" +
            "                <input class=\"inputBtn\" value=\"保存\" name=\"submit_endurance\" id=\"submit_endurance\" type=\"submit\">\n" +
            "            </td>\n" +
            "            <td valign=\"top\">\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        </tbody>\n" +
            "    </table>\n" +
            "</form>"


        const refreshSelect = () => {
            // 没有持久化信息则清空勾选
            const auth = endurance.getCookie("chii_auth");
            if (auth.length === 0) {
                columnA.querySelector("select[name=endurance]").value = 0;
                localStorage.removeItem("endurance");
            } else if (localStorage.getItem("endurance")) {
                columnA.querySelector("select[name=endurance]").value = 1;
            }
        }

        columnA.innerHTML = insertHtml + columnA.innerHTML;
        columnA.querySelector("#endurance").addEventListener("submit", (event) => {
            event.preventDefault();
            const selectedValue = event.target.querySelector("select[name=endurance]").value;
            if (selectedValue === 0) {
                endurance.removeCookie("chii_auth", "." + window.location.host);
                localStorage.setItem("endurance", "false");

                refreshSelect();
            } else {
                const auth = endurance.getCookie("chii_auth");
                if (auth.length === 0) {
                    localStorage.setItem("endurance", "false");
                    refreshSelect();

                    alert("当前环境持久化信息丢失，你需要手动退出重新登录，然后再到此处重新开启持久化！");
                } else {
                    endurance.setCookie("chii_auth", auth, "." + window.location.host, 360);
                    localStorage.setItem("endurance", "true");
                    refreshSelect();

                    alert("登录持久化成功，妈妈再也不用担心我掉登录状态啦！");
                }
            }
        });

        refreshSelect();
    }
}


function main() {
    init();
}

main();