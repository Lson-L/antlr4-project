package com.lson.antlr4.java.utils;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/13
 **/
public class AntlrWrapper<P extends Parser, L extends Lexer> {
    private final P parser;
    private final L lexer;

    private final TokenStream tokenStream;

    public AntlrWrapper(P parser, L lexer, TokenStream tokenStream) {
        this.parser = parser;
        this.lexer = lexer;
        this.tokenStream = tokenStream;
    }

    public P getParser() {
        return parser;
    }

    public L getLexer() {
        return lexer;
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }
}
