package com.intelligentquiz.memory.utils

import com.intelligentquiz.memory.database.entities.Question
import com.intelligentquiz.memory.database.entities.QuestionType

object QuestionParser {
    
    const val MAX_IMPORT_LENGTH = 100000
    const val MAX_QUESTION_COUNT = 1000
    
    data class ParseResult(
        val questions: List<ParsedQuestion>,
        val errors: List<String>,
        val totalParsed: Int,
        val fillInBlankCount: Int,
        val shortAnswerCount: Int
    )
    
    data class ParsedQuestion(
        val type: QuestionType,
        val content: String,
        val answer: String,
        val originalText: String
    )
    
    fun parseText(text: String, libraryId: Long): ParseResult {
        if (text.length > MAX_IMPORT_LENGTH) {
            return ParseResult(
                questions = emptyList(),
                errors = listOf("导入文本超过${MAX_IMPORT_LENGTH}字符限制"),
                totalParsed = 0,
                fillInBlankCount = 0,
                shortAnswerCount = 0
            )
        }
        
        val questions = mutableListOf<ParsedQuestion>()
        val errors = mutableListOf<String>()
        
        val lines = text.split("\n")
        var lineNumber = 0
        
        for (line in lines) {
            lineNumber++
            val trimmedLine = line.trim()
            
            if (trimmedLine.isEmpty()) continue
            
            try {
                val parsedQuestion = parseSingleLine(trimmedLine)
                if (parsedQuestion != null) {
                    questions.add(parsedQuestion)
                    
                    if (questions.size >= MAX_QUESTION_COUNT) {
                        errors.add("已达到最大题目数量限制($MAX_QUESTION_COUNT)，停止解析")
                        break
                    }
                }
            } catch (e: Exception) {
                errors.add("第${lineNumber}行解析错误: ${e.message}")
            }
        }
        
        val fillInBlankCount = questions.count { it.type == QuestionType.FILL_IN_BLANK }
        val shortAnswerCount = questions.count { it.type == QuestionType.SHORT_ANSWER }
        
        return ParseResult(
            questions = questions,
            errors = errors,
            totalParsed = questions.size,
            fillInBlankCount = fillInBlankCount,
            shortAnswerCount = shortAnswerCount
        )
    }
    
    private fun parseSingleLine(line: String): ParsedQuestion? {
        val fillInBlankQuestion = parseFillInBlank(line)
        if (fillInBlankQuestion != null) {
            return fillInBlankQuestion
        }
        
        val shortAnswerQuestion = parseShortAnswer(line)
        if (shortAnswerQuestion != null) {
            return shortAnswerQuestion
        }
        
        return null
    }
    
    private fun parseFillInBlank(line: String): ParsedQuestion? {
        val parenthesesPattern = Regex("""^(.+?)\((.+?)\)(.*)$""")
        val underlinePattern = Regex("""^(.+?)_{2,}(.*)$""")
        
        val parenthesesMatch = parenthesesPattern.find(line)
        if (parenthesesMatch != null) {
            val (prefix, answer, suffix) = parenthesesMatch.destructured
            val content = "${prefix.trim()}____${suffix.trim()}".replace("  ", " ").trim()
            
            return ParsedQuestion(
                type = QuestionType.FILL_IN_BLANK,
                content = content,
                answer = answer.trim(),
                originalText = line
            )
        }
        
        val underlineMatch = underlinePattern.find(line)
        if (underlineMatch != null) {
            val (questionPart, answerPart) = underlineMatch.destructured
            
            val parts = answerPart.split(Regex("""[，,；;。]""")).map { it.trim() }
            val firstPart = parts.firstOrNull()?.takeIf { it.isNotEmpty() }
            
            if (firstPart != null) {
                return ParsedQuestion(
                    type = QuestionType.FILL_IN_BLANK,
                    content = "${questionPart.trim()}____",
                    answer = firstPart,
                    originalText = line
                )
            }
        }
        
        return null
    }
    
    private fun parseShortAnswer(line: String): ParsedQuestion? {
        val patterns = listOf(
            Regex("""^(.+?)[？\?]\s*(.+)$"""),
            Regex("""^(.+?)[：:]\s*(.+)$"""),
            Regex("""^问[题：:\s]*(.+?)[？\?]\s*答[案：:\s]*(.+)$""")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(line)
            if (match != null) {
                val question = match.groupValues[1].trim()
                val answer = match.groupValues[2].trim()
                
                if (question.isNotEmpty() && answer.isNotEmpty()) {
                    return ParsedQuestion(
                        type = QuestionType.SHORT_ANSWER,
                        content = if (question.endsWith("？") || question.endsWith("?")) question else "$question？",
                        answer = answer,
                        originalText = line
                    )
                }
            }
        }
        
        return null
    }
    
    fun convertToQuestions(parsedQuestions: List<ParsedQuestion>, libraryId: Long): List<Question> {
        return parsedQuestions.map { parsed ->
            Question(
                libraryId = libraryId,
                type = parsed.type,
                content = parsed.content,
                answer = parsed.answer,
                originalText = parsed.originalText,
                difficulty = 1,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )
        }
    }
    
    fun validateImportText(text: String): List<String> {
        val errors = mutableListOf<String>()
        
        if (text.isEmpty()) {
            errors.add("导入文本不能为空")
        }
        
        if (text.length > MAX_IMPORT_LENGTH) {
            errors.add("文本长度超过${MAX_IMPORT_LENGTH}字符限制")
        }
        
        if (text.lines().count { it.trim().isNotEmpty() } > MAX_QUESTION_COUNT) {
            errors.add("题目数量可能超过${MAX_QUESTION_COUNT}题限制")
        }
        
        return errors
    }
}