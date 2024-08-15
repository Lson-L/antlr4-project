// Generated from D:/workspace/antlr4-project/src/main/resources/LsonParser.g4 by ANTLR 4.13.1
package com.lson.antlr4.test;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LsonParserParser}.
 */
public interface LsonParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LsonParserParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(LsonParserParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link LsonParserParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(LsonParserParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link LsonParserParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(LsonParserParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LsonParserParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(LsonParserParser.ExprContext ctx);
}