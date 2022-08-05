## Proyecto INF 329 (Compiladores)

1. **Cuerpo**
* BNF

		cuerpo --> cuerpo1 cuerpo | lambda
		cuerpo1 --> declaracion | procedimiento | lambda
	
* Conjunto First

		F(cuerpo1) = {VAR, PROCEDURE, lambda}
		F(cuerpo) = {VAR, PROCEDURE, lambda}

2. **Declaración**
* BNF

		declaracion --> VAR linea masLinea
		masLinea --> linea masLinea | lambda
		Linea --> ID masID: TIPO;
		masID --> , ID masID | lambda

* Conjunto First

		F(Linea) = {ID}
		F(masID) = { , , lambda}
		F(masLinea) = {ID, lambda}
		F(declaracion) = {VAR}

3. **Asignacion-Llamada**
* BNF

		asignacionLlamada --> ID combinacion
		combinacion --> asignacion | llamada

* Conjunto First

		F(combinacion) = {:=, (}
		F(asignacionLlamada) = {ID}

4. **Asignación**
* BNF

		asignacion --> := Expr; masAsignacion
		masAsignacion --> ID asignacion masAsignacion;

5. **Llamada**
* BNF

		llamada --> ();

6. **Condicional**
* BNF

		condicional --> IF (ExprBoole) THEN condicional1
		condicional1 --> sentenciaUnica Condicional2 | BEGIN sentencia END condicional3;
		condicioanl2 --> else sentenciaUnica | lambda
		condicional3 --> else BEGIN sentencia END | lambda

7. **Impresión** 
* BNF

		impresion --> WRITELN (elem masElem);
		masElem --> , masElem | lambda
		elem = STRINGCTTE | Expr

8. **Bucle For**
* BNF

		bucleFor --> FOR ID := Expr bucleFor1 Expr DO bucleFor2
		bucleFor1 --> TO | DOWNTO
		bucleFor2 --> BEGIN sentencia END; | sentenciaUnica

9. **Bucle While**
* BNF

		bucleWhile --> WHILE ExprBoole DO bucleWhile1
		bucleWhile1 --> setenciaUnica | BEGIN sentencia END;

10. **Bucle Repeat-Until**
* BNF

		bucleRepeat --> REPEAT sentencia UNTIL ExprBoole;

11. **Lectura**
* BNF

		lectura --> READLN (ID masID);
		masID --> , ID masID | lambda

12. **Sentencia**
* BNF

		sentencia --> sentencia1 masSentencia
		sentencia1 --> 
		masSentencia --> sentencia masSentencia | lambda

13. **Sentencia Única**
* BNF

		sentenciaUnica --> asignacionLlamada | lectura | impresion

* Conjunto First

		F(sentenciaUnica) = {ID, READLN, WRITELN}

14. **Expresión Aritmética**
* BNF

		Expr --> Termino Expr2
		Expr2 --> Expr1 Expr2 | lambda
		Exp1 --> +Termino | -Termino
		Termino --> Factor Termino2
		Termino2 --> Termino1 Termino2 | lambda
		Termino1 --> *Factor | /Factor | MOD Factor
		Factor --> ID | NUM | -Factor | +Factor | (Expr)

* Conjunto First

		F(Factor)   = {ID, NUM, -, +, (}
		F(Termino1) = {*, /, MOD}
		F(Termino2) = {*, /, MOD, lambda}
		F(Termino) = {ID, NUM, -, +, (}
		F(Expr1) = {+, -}
		F(Expr2) = {+, -, lambda}
		F(Expr) = {ID, NUM, -, +, (}

15. **Expresión Boole**
* BNF

		ExprBoole --> TermBoole ExprBoole1 
		ExprBoole1 --> OR TermBoole ExprBoole1 | lambda
		TermBoole --> FactorBoole TermBoole1
		TermBoole1 --> AND FactorBoole TermBoole1 | lambda
		FactorBoole --> Expr OPREL Expr | NOT FactorBoole

* Conjunto First

		F(FactorBoole) = {ID, NUM, -, +, (, NOT}
		F(TermBoole1) = {AND, lambda}
		F(TermBoole)  = {ID, NUM, -, +, (, NOT}
		F(ExprBoole1) = {OR, lambda}
		F(ExprBoole)  = {ID, NUM, -, +, (, NOT}

### Ricky Roy Ramirez Pineda
