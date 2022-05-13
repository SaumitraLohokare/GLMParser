package main;

import java.util.List;

public class Program {
    public static void main(String[] args) {
        String file = "./example.glm";
        
        List<Token> tokens = Tokenizer.tokenizeFile(file);
        int undefined = 0;
        for (Token token : tokens) {
            if (token.type == TokenType.UNDEFINED) undefined++;
            System.out.println(token);
        }
        System.out.println("Unable to identify " + undefined + " tokens.");
    }
}
