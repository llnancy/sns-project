package org.lilu.sns.service;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/1/27
 * @Description: 基于前缀树（又叫字典树）的敏感词过滤
 */
@Service
public class SensitiveService {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认的敏感词替换字符
     */
    private static final String DEFAULT_REPLACEMENT = "**";

    /**
     * 前缀树节点类
     */
    private class TrieNode {
        private boolean end = false;
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        public void addSubNode(Character key,TrieNode value) {
            subNodes.put(key,value);
        }
        public TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }
        public boolean isEnd() {
            return end;
        }
        public void setEnd(boolean end) {
            this.end = end;
        }
    }

    /**
     * 创建根节点
     */
    private TrieNode rootNode = new TrieNode();

    /**
     * 将一个词word加入前缀树
     * @param word
     */
    private void addWord(String word) {
        TrieNode tempNode = rootNode;
        int len = word.length();
        for (int i = 0; i < len; i++) {
            // 获取每一个字符
            Character c = word.charAt(i);
            // 空格（或其它特殊符号）直接跳过
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            // 该字符不在字典树中，需要加入
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = node;
            if (i == len - 1) {
                // 敏感词结束，设置结束标志。
                tempNode.setEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param target 待过滤的字符串
     * @return 返回敏感词被替换成DEFAULT_REPLACEMENT内容的字符串
     */
    public String filter(String target) {
        if (StringUtils.isEmpty(target)) {
            return target;
        }
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        int len = target.length();
        while (position < len) {
            char c = target.charAt(position);
            // 判断是否为特殊符号
            if (isSymbol(c)) {
                // 刚开始就是特殊符号，则begin和position都向后移动一位。
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                // 如果tempNode不为rootNode，则已经匹配了至少1个字符，需要忽略掉特殊符号，继续向后匹配
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin为开始的到position的字符串不存在敏感词
                result.append(target.charAt(begin));
                // 从begin的下一个字符开始再次重复过滤过程。
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isEnd()) {
                // 找到敏感词
                result.append(DEFAULT_REPLACEMENT);
                position++;
                begin = position;
                tempNode = rootNode;
            } else {
                position++;
            }
        }
        return result.toString();
    }

    /**
     * 过滤特殊符号
     *
     * CharUtils.isAsciiAlphanumeric(c)
     * if between 48 and 57 or 65 and 90 or 97 and 122 inclusive
     *
     *  [\u2E80-\u9FFF]+$ 匹配所有东亚区的语言，包含生僻字
     *  [\u4E00-\u9FFF]+$ 匹配简体和繁体，不包含生僻字
     *  [\u4E00-\u9FA5]+$ 匹配简体
     * @param c
     * @return 如果不在ASCII码范围并且不在东亚文字范围则返回true，反则反之。
     */
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && !c.toString().matches("[\\u2E80-\\u9FFF]");
    }

    /**
     * 初始化Bean对象时，读取文本资源，构建前缀树。
     *
     * 1、Spring为bean提供了两种初始化bean的方式，实现InitializingBean接口，实现afterPropertiesSet方法，或者在配置文件中通过init-method指定（使用@PostConstruct注解），两种方式可以同时使用。
     *
     * 2、实现InitializingBean接口是直接调用afterPropertiesSet方法，比通过反射调用init-method指定的方法效率要高一点，但是init-method方式消除了对spring的依赖。
     *
     * 3、如果调用afterPropertiesSet方法时出错，则不调用init-method指定的方法。
     */
    @PostConstruct
    public void init() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            // 使用当前类的类加载器加载资源文件
            is = getClass().getClassLoader().getResourceAsStream("SensitiveWord.txt");
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String word;
            // 每一行当做一个敏感词构建到前缀树中
            while ((word = reader.readLine()) != null) {
                word = word.trim();
                addWord(word);
            }
        } catch (Exception e) {
            logger.error("读取敏感词文件失败-->" + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("读取敏感词文件的IO流关闭失败-->" + e.getMessage());
            }
        }
    }

//    public static void main(String[] args) {
//        SensitiveService sensitiveService = new SensitiveService();
//        sensitiveService.addWord(" 色情");
//        sensitiveService.addWord("暴力 ");
//        sensitiveService.addWord("看 片");
//        sensitiveService.addWord("AV");
//        sensitiveService.addWord("敏感词汇");
//        String result = sensitiveService.filter("骚年，看片吗？不同的颜色●情况也不一样，很 ●暴*力的那种哦！不是A V啦。来个敏感词测试.");
//        System.out.println(result);
//    }
}