package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static final char[] DELIMITERS = {' ', '\n', '\t', '{', '}', ';'};
    private static boolean isDelimiter(char c) {
        for (char d : DELIMITERS) {
            if (c == d) return true;
        }
        return false;
    }

    private static boolean isToken(char delimiter) {
        for (int i = 3; i < DELIMITERS.length; i++) {
            if (delimiter == DELIMITERS[i]) return true;
        }
        return false;
    }

    private static final String[] KEYWORDS = {"clock", "object", "module"};
    private static boolean isKeyword(String s) {
        for (String kw : KEYWORDS) {
            if (kw.equals(s)) return true;
        }
        if (s.startsWith("#")) return true;
        return false;
    }

    public static List<Token> tokenizeString(String s) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (isDelimiter(s.charAt(i))) {
                String value = builder.toString().trim();
                if (value.length() > 0)
                    tokens.add(new Token(value, TokenType.UNDEFINED));
                builder.setLength(0);

                if (isToken(s.charAt(i))) {
                    TokenType t = TokenType.UNDEFINED;
                    if (s.charAt(i) == '{')         t = TokenType.LPAREN;
                    else if (s.charAt(i) == '}')    t = TokenType.RPAREN;
                    else if (s.charAt(i) == ';')    t = TokenType.SEMICOLON;

                    tokens.add(new Token(s.substring(i, i+1), t));
                }
            } else {
                builder.append(s.charAt(i));
            }
        }
        String value = builder.toString().trim();
        if (value.length() > 0)
            tokens.add(new Token(value, TokenType.UNDEFINED));

        return tokens;
    }

    public static List<Token> tokenizeFile(String file) {
        List<Token> tokens = new ArrayList<>();

        try {
            List<String> stream = Files.readAllLines(Paths.get(file));

            for (String s: stream) {
                if (s.trim().startsWith("//")) continue;
                tokens.addAll(tokenizeString(s));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tokens.size() == 0) return tokens;
        
        // Need to go through each token and set TokenType
        // Rules: 
        // 1. If Keyword, then set TokenType to KEYWORD
        // 2. If previous token Keyowrd then set current to IDENTIFIER.
        // 3. If previous token was delimiter && current is not keyword, set current to IDENTIFIER
        // 4. If previous token was IDENTIFIER && current is not eny of above then set to LITERAL
        // 5. If previous was LITERAL && current is UNDEFINED then set current to LITERAL

        // TODO: Need to update the rules a bit more so that we allow 2+ literals for an identifier.


        Token prev = null;
        Token tok = null;
        for (int i = 0; i < tokens.size(); i++) {
            tok = tokens.get(i);
            if (tok.type != TokenType.UNDEFINED) {
                prev = tok;
                continue;
            }

            if (isKeyword(tok.value)) {
                tok.setType(TokenType.KEYWORD);
                prev = tok;
                continue;
            }

            if (prev != null) {
                if (prev.type == TokenType.KEYWORD) {
                    tok.setType(TokenType.IDENTIFIER);
                } else if (prev.type == TokenType.RPAREN || prev.type == TokenType.LPAREN || prev.type == TokenType.SEMICOLON) {
                    tok.setType(TokenType.IDENTIFIER);
                } else if (prev.type == TokenType.IDENTIFIER) {
                    tok.setType(TokenType.LITERAL);
                } else if (prev.type == TokenType.LITERAL) {
                    tok.setType(TokenType.LITERAL);
                }
            }

            prev = tok;
        }

        return tokens;
    }
}
