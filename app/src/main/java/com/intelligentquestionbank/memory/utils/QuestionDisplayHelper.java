package com.intelligentquestionbank.memory.utils;

import com.intelligentquestionbank.memory.database.entities.Question;

public class QuestionDisplayHelper {

    public static String getQuestionForMemoryTest(Question question) {
        if (question.getType().equals("fill_in_blank")) {
            // 填空题：隐藏括号中的答案，显示为下划线
            return question.getContent().replaceAll("\\([^)]+\\)", "____");
        } else {
            // 简答题：直接显示题目
            return question.getContent();
        }
    }

    public static String getQuestionWithAnswer(Question question) {
        if (question.getType().equals("fill_in_blank")) {
            // 填空题：显示完整题目（包含答案）
            return question.getContent();
        } else {
            // 简答题：题目 + 答案
            return question.getContent() + "\n\n答案：" + question.getAnswer();
        }
    }

    public static String getAnswerText(Question question) {
        if (question.getType().equals("fill_in_blank")) {
            // 填空题：显示填入答案的完整题目
            return question.getContent();
        } else {
            // 简答题：显示标准答案
            return question.getAnswer();
        }
    }

    public static String getQuestionOnly(Question question) {
        if (question.getType().equals("fill_in_blank")) {
            // 填空题：将答案替换为下划线
            return question.getContent().replaceAll("\\([^)]+\\)", "____");
        } else {
            // 简答题：只显示题目部分
            return question.getContent();
        }
    }

    public static String extractAnswer(Question question) {
        if (question.getType().equals("fill_in_blank")) {
            // 从括号中提取答案
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(([^)]+)\\)");
            java.util.regex.Matcher matcher = pattern.matcher(question.getContent());
            StringBuilder answers = new StringBuilder();
            while (matcher.find()) {
                if (answers.length() > 0) {
                    answers.append(", ");
                }
                answers.append(matcher.group(1));
            }
            return answers.toString();
        } else {
            return question.getAnswer();
        }
    }
}