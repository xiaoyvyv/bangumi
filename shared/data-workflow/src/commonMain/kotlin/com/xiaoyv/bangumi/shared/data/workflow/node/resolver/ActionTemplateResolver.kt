package com.xiaoyv.bangumi.shared.data.workflow.node.resolver

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionTemplateRoot
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull

/**
 * 工业级可扩展的模板解析与表达式求值引擎。
 *
 * 支持占位符插值 `${...}`、上下文路径导航、行内算术运算、比较运算、逻辑运算及三元 / Elvis 表达式：
 * - 路径引用：`${input.title}`、`${vars.count}`、`${steps.request.body.name}`
 * - 算术运算：`${loop.index + 1}`、`${vars.score * 0.9}`
 * - 比较逻辑：`${loop.index > 0}`、`${vars.score >= 80}`、`${vars.tag == 'anime'}`
 * - 逻辑组合：`${loop.index > 0 && vars.score >= 90}`
 * - Elvis 兜底：`${vars.title ?: '默认标题'}`
 * - 三元选择：`${vars.score >= 80 ? '优秀' : '普通'}`
 * - 成员属性与方法：`${vars.items.length}`、`${vars.text.trim}`、`${vars.text.length}`
 */
object ActionTemplateResolver {
    private val placeholderRegex = Regex("\\$\\{([^}]+)\\}")

    /**
     * 解析文本中的 `${...}` 占位符。
     */
    fun resolveText(template: String, context: ActionExecutionContext): String {
        return placeholderRegex.replace(template) { match ->
            val expr = match.groupValues[1]
            val evaluated = evaluateExpression(expr, context)
            evaluated.asText()
        }
    }

    /**
     * 解析 JSON 节点。单占位符完整匹配时保留原始 JSON 类型，包含文本插值时返回 JsonPrimitive 字符串。
     */
    fun resolveElement(value: JsonElement?, context: ActionExecutionContext): JsonElement {
        if (value is JsonObject) return JsonObject(value.mapValues { (_, element) -> resolveElement(element, context) })
        if (value is JsonArray) return JsonArray(value.map { resolveElement(it, context) })
        val primitive = value as? JsonPrimitive ?: return value ?: JsonNull
        val content = primitive.contentOrNull ?: return primitive
        val exact = placeholderRegex.matchEntire(content)
        return when {
            exact != null -> evaluateExpression(exact.groupValues[1], context)
            "\${" in content -> JsonPrimitive(resolveText(content, context))
            else -> primitive
        }
    }

    /**
     * 执行表达式求值。
     */
    fun evaluateExpression(expression: String, context: ActionExecutionContext): JsonElement {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) return JsonNull
        return try {
            ExpressionParser(trimmed, context).parse()
        } catch (e: Exception) {
            resolvePath(trimmed, context)
        }
    }

    internal fun resolvePath(path: String, context: ActionExecutionContext): JsonElement {
        val parts = path.split('.').filter(String::isNotBlank)
        if (parts.isEmpty()) return JsonNull
        val root: JsonElement = when (parts.first()) {
            ActionTemplateRoot.INPUT -> context.input
            ActionTemplateRoot.ENVIRONMENT -> context.environment
            ActionTemplateRoot.TRIGGER -> context.trigger
            ActionTemplateRoot.VARIABLES -> JsonObject(context.variables)
            ActionTemplateRoot.STEP_OUTPUTS -> JsonObject(context.stepOutputs)
            ActionTemplateRoot.LOOP -> context.loop
            else -> return JsonNull
        }
        return parts.drop(1).fold(root) { value, key ->
            when (value) {
                is JsonObject -> value[key] ?: JsonNull
                is JsonArray -> key.toIntOrNull()?.let(value::getOrNull) ?: JsonNull
                else -> JsonNull
            }
        }
    }

    internal fun JsonElement.asText(): String = when (this) {
        is JsonPrimitive -> contentOrNull.orEmpty()
        JsonNull -> ""
        else -> toString()
    }
}

private class ExpressionParser(
    private val input: String,
    private val context: ActionExecutionContext,
) {
    private var index = 0

    fun parse(): JsonElement {
        val result = parseTernary()
        skipWhitespace()
        return result
    }

    private fun parseTernary(): JsonElement {
        val condition = parseElvis()
        skipWhitespace()
        if (peek() == '?' && peekNext() != ':') {
            consume('?')
            val trueBranch = parseTernary()
            skipWhitespace()
            if (peek() == ':') {
                consume(':')
                val falseBranch = parseTernary()
                return if (condition.isTruthy()) trueBranch else falseBranch
            }
        }
        return condition
    }

    private fun parseElvis(): JsonElement {
        var left = parseOr()
        while (true) {
            skipWhitespace()
            if (peek() == '?' && peekNext() == ':') {
                consume('?')
                consume(':')
                val right = parseOr()
                left = if (left.isNotNullOrEmpty()) left else right
            } else {
                break
            }
        }
        return left
    }

    private fun parseOr(): JsonElement {
        var left = parseAnd()
        while (true) {
            skipWhitespace()
            if (peek() == '|' && peekNext() == '|') {
                consume('|')
                consume('|')
                val right = parseAnd()
                left = JsonPrimitive(left.isTruthy() || right.isTruthy())
            } else {
                break
            }
        }
        return left
    }

    private fun parseAnd(): JsonElement {
        var left = parseEquality()
        while (true) {
            skipWhitespace()
            if (peek() == '&' && peekNext() == '&') {
                consume('&')
                consume('&')
                val right = parseEquality()
                left = JsonPrimitive(left.isTruthy() && right.isTruthy())
            } else {
                break
            }
        }
        return left
    }

    private fun parseEquality(): JsonElement {
        var left = parseRelational()
        while (true) {
            skipWhitespace()
            if (peek() == '=' && peekNext() == '=') {
                consume('=')
                consume('=')
                val right = parseRelational()
                left = JsonPrimitive(areJsonElementsEqual(left, right))
            } else if (peek() == '!' && peekNext() == '=') {
                consume('!')
                consume('=')
                val right = parseRelational()
                left = JsonPrimitive(!areJsonElementsEqual(left, right))
            } else {
                break
            }
        }
        return left
    }

    private fun parseRelational(): JsonElement {
        var left = parseAdditive()
        while (true) {
            skipWhitespace()
            val c = peek()
            val next = peekNext()
            if (c == '>' && next == '=') {
                consume('>')
                consume('=')
                val right = parseAdditive()
                left = JsonPrimitive(compareElements(left, right) >= 0)
            } else if (c == '<' && next == '=') {
                consume('<')
                consume('=')
                val right = parseAdditive()
                left = JsonPrimitive(compareElements(left, right) <= 0)
            } else if (c == '>' && next != '=') {
                consume('>')
                val right = parseAdditive()
                left = JsonPrimitive(compareElements(left, right) > 0)
            } else if (c == '<' && next != '=') {
                consume('<')
                val right = parseAdditive()
                left = JsonPrimitive(compareElements(left, right) < 0)
            } else {
                break
            }
        }
        return left
    }

    private fun parseAdditive(): JsonElement {
        var left = parseMultiplicative()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '+') {
                consume('+')
                val right = parseMultiplicative()
                left = addElements(left, right)
            } else if (c == '-') {
                consume('-')
                val right = parseMultiplicative()
                left = subtractElements(left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseMultiplicative(): JsonElement {
        var left = parseUnary()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '*') {
                consume('*')
                val right = parseUnary()
                left = multiplyElements(left, right)
            } else if (c == '/') {
                consume('/')
                val right = parseUnary()
                left = divideElements(left, right)
            } else if (c == '%') {
                consume('%')
                val right = parseUnary()
                left = moduloElements(left, right)
            } else {
                break
            }
        }
        return left
    }

    private fun parseUnary(): JsonElement {
        skipWhitespace()
        val c = peek()
        if (c == '!') {
            consume('!')
            val operand = parseUnary()
            return JsonPrimitive(!operand.isTruthy())
        } else if (c == '-' && (peekNext().isDigit() || peekNext() == '.')) {
            // Negative number literal handling inside parsePrimary
        } else if (c == '-') {
            consume('-')
            val operand = parseUnary()
            val num = operand.asDouble() ?: return JsonNull
            val neg = -num
            return if (neg % 1.0 == 0.0) JsonPrimitive(neg.toLong()) else JsonPrimitive(neg)
        }
        return parsePostfix()
    }

    private fun parsePostfix(): JsonElement {
        var expr = parsePrimary()
        while (true) {
            skipWhitespace()
            if (peek() == '.') {
                consume('.')
                val member = parseIdentifierString()
                expr = evaluateMemberAccess(expr, member)
            } else if (peek() == '[') {
                consume('[')
                val indexExpr = parseTernary()
                skipWhitespace()
                if (peek() == ']') consume(']')
                expr = evaluateIndexAccess(expr, indexExpr)
            } else {
                break
            }
        }
        return expr
    }

    private fun parsePrimary(): JsonElement {
        skipWhitespace()
        val c = peek()
        if (c == '(') {
            consume('(')
            val expr = parseTernary()
            skipWhitespace()
            if (peek() == ')') consume(')')
            return expr
        }
        if (c == '\'' || c == '"') {
            return JsonPrimitive(parseStringLiteral())
        }
        if (c.isDigit() || (c == '-' && peekNext().isDigit())) {
            return JsonPrimitive(parseNumberLiteral())
        }

        val id = parseIdentifierString()
        if (id.isEmpty()) return JsonNull

        return when (id) {
            "true" -> JsonPrimitive(true)
            "false" -> JsonPrimitive(false)
            "null" -> JsonNull
            ActionTemplateRoot.INPUT,
            ActionTemplateRoot.ENVIRONMENT,
            ActionTemplateRoot.TRIGGER,
            ActionTemplateRoot.VARIABLES,
            ActionTemplateRoot.STEP_OUTPUTS,
            ActionTemplateRoot.LOOP -> {
                var current: JsonElement = when (id) {
                    ActionTemplateRoot.INPUT -> context.input
                    ActionTemplateRoot.ENVIRONMENT -> context.environment
                    ActionTemplateRoot.TRIGGER -> context.trigger
                    ActionTemplateRoot.VARIABLES -> JsonObject(context.variables)
                    ActionTemplateRoot.STEP_OUTPUTS -> JsonObject(context.stepOutputs)
                    ActionTemplateRoot.LOOP -> context.loop
                    else -> JsonNull
                }

                while (peek() == '.' || peek() == '[') {
                    if (peek() == '.') {
                        val savedIndex = index
                        consume('.')
                        val prop = parseIdentifierString()
                        if (prop.isEmpty()) {
                            index = savedIndex
                            break
                        }
                        if (current is JsonObject && current.containsKey(prop)) {
                            current = current.getValue(prop)
                        } else if (current is JsonArray && prop.toIntOrNull() != null) {
                            val i = prop.toInt()
                            current = current.getOrNull(i) ?: JsonNull
                        } else {
                            // Backtrack so parsePostfix can evaluate member properties/methods
                            index = savedIndex
                            break
                        }
                    } else if (peek() == '[') {
                        val savedIndex = index
                        consume('[')
                        val indexVal = parseTernary()
                        skipWhitespace()
                        if (peek() == ']') {
                            consume(']')
                            current = evaluateIndexAccess(current, indexVal)
                        } else {
                            index = savedIndex
                            break
                        }
                    }
                }
                current
            }
            else -> JsonPrimitive(id)
        }
    }

    private fun parseStringLiteral(): String {
        val quote = consume()
        val sb = StringBuilder()
        while (index < input.length) {
            val c = consume()
            if (c == quote) break
            if (c == '\\' && index < input.length) {
                when (val escaped = consume()) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    '\\' -> sb.append('\\')
                    '\'' -> sb.append('\'')
                    '"' -> sb.append('"')
                    else -> sb.append(escaped)
                }
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun parseNumberLiteral(): Number {
        val start = index
        if (peek() == '-') consume()
        while (index < input.length && peek().isDigit()) consume()
        if (index < input.length && peek() == '.' && peekNext().isDigit()) {
            consume()
            while (index < input.length && peek().isDigit()) consume()
            val text = input.substring(start, index)
            return text.toDoubleOrNull() ?: 0.0
        }
        val text = input.substring(start, index)
        return text.toLongOrNull() ?: text.toDoubleOrNull() ?: 0
    }

    private fun parseIdentifierString(): String {
        skipWhitespace()
        val start = index
        while (index < input.length) {
            val c = peek()
            if (c.isLetterOrDigit() || c == '_' || c == '$') {
                consume()
            } else {
                break
            }
        }
        return input.substring(start, index)
    }

    private fun skipWhitespace() {
        while (index < input.length && input[index].isWhitespace()) {
            index++
        }
    }

    private fun peek(): Char = if (index < input.length) input[index] else '\u0000'
    private fun peekNext(): Char = if (index + 1 < input.length) input[index + 1] else '\u0000'
    private fun consume(): Char = if (index < input.length) input[index++] else '\u0000'
    private fun consume(expected: Char) {
        if (peek() == expected) index++
    }

    private fun evaluateMemberAccess(target: JsonElement, member: String): JsonElement {
        return when (target) {
            is JsonArray -> when (member) {
                "length", "size" -> JsonPrimitive(target.size)
                "isEmpty", "empty" -> JsonPrimitive(target.isEmpty())
                "isNotEmpty" -> JsonPrimitive(target.isNotEmpty())
                "first" -> target.firstOrNull() ?: JsonNull
                "last" -> target.lastOrNull() ?: JsonNull
                else -> member.toIntOrNull()?.let(target::getOrNull) ?: JsonNull
            }
            is JsonObject -> when (member) {
                "length", "size" -> JsonPrimitive(target.size)
                "isEmpty", "empty" -> JsonPrimitive(target.isEmpty())
                "isNotEmpty" -> JsonPrimitive(target.isNotEmpty())
                "keys" -> JsonArray(target.keys.map { JsonPrimitive(it) })
                else -> target[member] ?: JsonNull
            }
            is JsonPrimitive -> {
                val str = target.contentOrNull.orEmpty()
                when (member) {
                    "length", "size" -> JsonPrimitive(str.length)
                    "isEmpty", "empty" -> JsonPrimitive(str.isEmpty())
                    "isNotEmpty" -> JsonPrimitive(str.isNotEmpty())
                    "isNotBlank" -> JsonPrimitive(str.isNotBlank())
                    "isBlank" -> JsonPrimitive(str.isBlank())
                    "uppercase", "toUpperCase" -> JsonPrimitive(str.uppercase())
                    "lowercase", "toLowerCase" -> JsonPrimitive(str.lowercase())
                    "trim" -> JsonPrimitive(str.trim())
                    else -> JsonNull
                }
            }
        }
    }

    private fun evaluateIndexAccess(target: JsonElement, indexVal: JsonElement): JsonElement {
        return when (target) {
            is JsonArray -> {
                val i = indexVal.asText().toIntOrNull() ?: return JsonNull
                target.getOrNull(i) ?: JsonNull
            }
            is JsonObject -> {
                val key = indexVal.asText()
                target[key] ?: JsonNull
            }
            else -> JsonNull
        }
    }
}

private fun JsonElement.isTruthy(): Boolean = when (this) {
    is JsonPrimitive -> {
        booleanOrNull ?: doubleOrNull?.let { it != 0.0 } ?: intOrNull?.let { it != 0 } ?: contentOrNull?.isNotBlank() ?: false
    }
    is JsonObject -> isNotEmpty()
    is JsonArray -> isNotEmpty()
    JsonNull -> false
}

private fun JsonElement.isNotNullOrEmpty(): Boolean = when (this) {
    JsonNull -> false
    is JsonPrimitive -> contentOrNull?.isNotEmpty() == true
    is JsonObject -> isNotEmpty()
    is JsonArray -> isNotEmpty()
}

private fun JsonElement.asDouble(): Double? = when (this) {
    is JsonPrimitive -> {
        if (booleanOrNull != null) return if (booleanOrNull == true) 1.0 else 0.0
        if (intOrNull != null) return intOrNull!!.toDouble()
        if (doubleOrNull != null) return doubleOrNull!!
        val content = contentOrNull ?: return null
        content.toDoubleOrNull()
    }
    else -> null
}

private fun JsonElement.isNumericPrimitive(): Boolean = when (this) {
    is JsonPrimitive -> intOrNull != null || doubleOrNull != null || (contentOrNull != null && contentOrNull!!.toDoubleOrNull() != null)
    else -> false
}

private fun areJsonElementsEqual(left: JsonElement, right: JsonElement): Boolean {
    if (left == right) return true
    val leftNum = left.asDouble()
    val rightNum = right.asDouble()
    if (leftNum != null && rightNum != null && left.isNumericPrimitive() && right.isNumericPrimitive()) {
        return leftNum == rightNum
    }
    return left.asText() == right.asText()
}

private fun compareElements(left: JsonElement, right: JsonElement): Int {
    val leftNum = left.asDouble()
    val rightNum = right.asDouble()
    if (leftNum != null && rightNum != null && left.isNumericPrimitive() && right.isNumericPrimitive()) {
        return leftNum.compareTo(rightNum)
    }
    return left.asText().compareTo(right.asText())
}

private fun addElements(left: JsonElement, right: JsonElement): JsonElement {
    if (left is JsonNull || right is JsonNull) return JsonNull
    val leftNum = left.asDouble()
    val rightNum = right.asDouble()
    if (leftNum != null && rightNum != null && left.isNumericPrimitive() && right.isNumericPrimitive()) {
        val sum = leftNum + rightNum
        return if (sum % 1.0 == 0.0) JsonPrimitive(sum.toLong()) else JsonPrimitive(sum)
    }
    return JsonPrimitive(left.asText() + right.asText())
}

private fun subtractElements(left: JsonElement, right: JsonElement): JsonElement {
    if (left is JsonNull || right is JsonNull) return JsonNull
    val leftNum = left.asDouble() ?: 0.0
    val rightNum = right.asDouble() ?: 0.0
    val diff = leftNum - rightNum
    return if (diff % 1.0 == 0.0) JsonPrimitive(diff.toLong()) else JsonPrimitive(diff)
}

private fun multiplyElements(left: JsonElement, right: JsonElement): JsonElement {
    if (left is JsonNull || right is JsonNull) return JsonNull
    val leftNum = left.asDouble() ?: 0.0
    val rightNum = right.asDouble() ?: 0.0
    val prod = leftNum * rightNum
    return if (prod % 1.0 == 0.0) JsonPrimitive(prod.toLong()) else JsonPrimitive(prod)
}

private fun divideElements(left: JsonElement, right: JsonElement): JsonElement {
    if (left is JsonNull || right is JsonNull) return JsonNull
    val leftNum = left.asDouble() ?: 0.0
    val rightNum = right.asDouble() ?: 0.0
    if (rightNum == 0.0) return JsonPrimitive(0)
    val quot = leftNum / rightNum
    return if (quot % 1.0 == 0.0) JsonPrimitive(quot.toLong()) else JsonPrimitive(quot)
}

private fun moduloElements(left: JsonElement, right: JsonElement): JsonElement {
    if (left is JsonNull || right is JsonNull) return JsonNull
    val leftNum = left.asDouble() ?: 0.0
    val rightNum = right.asDouble() ?: 0.0
    if (rightNum == 0.0) return JsonPrimitive(0)
    val mod = leftNum % rightNum
    return if (mod % 1.0 == 0.0) JsonPrimitive(mod.toLong()) else JsonPrimitive(mod)
}

private fun JsonElement.asText(): String = when (this) {
    is JsonPrimitive -> contentOrNull.orEmpty()
    JsonNull -> ""
    else -> toString()
}
