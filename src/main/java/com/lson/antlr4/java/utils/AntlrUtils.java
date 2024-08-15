package com.lson.antlr4.java.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 *
 * @author lson
 *
 * @date 2024/8/13
 **/
public class AntlrUtils {

    public static <P extends Parser, L extends Lexer> AntlrWrapper<P, L> getAntlrWrapper(Function<CharStream, L> lexerFunction, Function<TokenStream, P> parserFunction, CharStream charStream) {
        L lexer = lexerFunction.apply(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        P parser = parserFunction.apply(tokens);
        return new AntlrWrapper<>(parser, lexer, tokens);
    }

    public static <P extends Parser, L extends Lexer> AntlrWrapper<P, L> getAntlrWrapper(Function<CharStream, L> lexerFunction, Function<TokenStream, P> parserFunction, String text) {
        return getAntlrWrapper(lexerFunction, parserFunction, CharStreams.fromString(text));
    }
}
