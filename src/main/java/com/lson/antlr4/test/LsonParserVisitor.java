// Generated from D:/workspace/antlr4-project/src/main/resources/LsonParser.g4 by ANTLR 4.13.1
package com.lson.antlr4.test;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LsonParserParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LsonParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LsonParserParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(LsonParserParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link LsonParserParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(LsonParserParser.ExprContext ctx);
}