package main;

public class Token {
    public String value;
    public TokenType type;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public void setType(TokenType t) { type = t; }

    public String toString() { return "(\"" + value + "\", " + type.name() + ")"; }
}
