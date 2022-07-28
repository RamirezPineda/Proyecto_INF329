package Parsing;

/**@param Ricky Roy Ramirez Pineda
 * 218158254
 * INF329-SA (Compiladores)
 * 25/07/2022 0:50
 * Horas invertidas: 12 hrs
 **/

public class Parser {
    private Analex analex;
    private ErrorMgr errorMgr;
    
    public Parser(){
        errorMgr = new ErrorMgr();
        analex = new Analex(errorMgr);
    }
    
    public void init(String progFuente){    
        errorMgr.init();
        analex.init(progFuente);
        
        programa();     //Llamar al símbolo inicial de la BNF.
    }
    
    public boolean hayError(){
        return errorMgr.hayError();
    }
    
    public void comunicarError(){
        onComunicar(errorMgr.getPosLexema(), errorMgr.getLexema().length(), errorMgr.getErrorMsj());
    }
    
    public void onComunicar(int posLexema, int longitud, String errorMsg){   
        //Overridable (Para el front-end)       
    }
    
//---------    
    private void programa(){ //Símbolo inicial. programa->Header Cuerpo Main
            //Seguir única sección
        header();
        cuerpo();
        main();
    }
    
    private void header(){  //header -> PROGRAM ID; | lambda
        if (analex.preNom()==Token.PROGRAM){
            //Seguir 1era seccion: PROGRAM ID ;
           match(Token.PROGRAM, "Se espera PROGRAM");
           match(Token.ID, "Se espera un ID");
           match(Token.PTOCOMA, "Falta ;");
        }
        else //Seguir 2da sección: lambda
          ; 
    }
    
    private void cuerpo(){
        //Esta seccion puede estar vacia
        if (analex.preNom() == Token.VAR || analex.preNom() == Token.PROCEDURE) {
            //1era seccion
            //el cuerpo puede tener cero o mas declaraciones
            //el cuerpo puede tener cero o mas procedimientos
            cuerpo1();
            cuerpo();  
        } //else lambda 2da seccion
        
    }
    
    private void main(){
        //OBLIGATORIO
        match(Token.BEGIN, "Se espera BEGIN");
        sentencia(); //El bloque puede estar vacío (lambda)
        match(Token.END, "Se espera END");
        match(Token.PTO, "Falta .");
    }
    
    private void match(int tokenNom, String errorMsj){
        if (analex.preNom() == tokenNom)
            analex.avanzar();
        else
            errorMgr.setError(errorMsj, analex.getPosLexema(), analex.lexema());
    }
    
    /*==========================================================================
                      METODOS IMPLEMENTADOS POR MI 
    ===========================================================================*/
    
    //==============================BNF DECL====================================
    /*Declaracion de una(s) linea(s)
    VAR
        a, b, c, d: INTENER;
        e, f : BOOLEAN;
    */   
    private void declaracion() {
        if (analex.preNom() == Token.VAR) {//1era seccion
            match(Token.VAR, "Se espera VAR");
            linea();
            masLinea();
        } //else lambda //2da seccion
    }
    
    private void masLinea() {
        if (analex.preNom() == Token.ID) { //1era seccion
            linea();
            masLinea();
        } //else  lambda 2da seccion  
    }

    private void linea() {
        match(Token.ID, "Se espera un ID");
        masID();
        match(Token.DOSPTOS, "Se espera :");
        match(Token.TIPO, "Se espera un TIPO");
        match(Token.PTOCOMA, "Falta ;");
    }

    private void masID() {
        if (analex.preNom() == Token.COMA) { //1era seccion
            match(Token.COMA, "Falta ,");
            match(Token.ID, "Se espera un ID");
            masID();
        } //else lambda 2da seccion
    }
    
    //==============================BNF PROC====================================
    
    private void procedimiento() {
        if (analex.preNom() == Token.PROCEDURE) { //1era seccion
            match(Token.PROCEDURE, "Se espera PROCEDURE");
            match(Token.ID, "Se espera un ID");
            match(Token.PTOCOMA, "Falta ;");
            match(Token.BEGIN, "Se espera BEGIN");
            sentencia();
            match(Token.END, "Se espera END");
            match(Token.PTOCOMA, "Falta ;");
        } //else lambda 2da seccion
    }
     
    //========================================================================== 
    
    private void cuerpo1() {
        if (analex.preNom() == Token.VAR) //1era seccion
            declaracion();
        else {
            if (analex.preNom() == Token.PROCEDURE) //2da seccion
                procedimiento();
            //else lambda 3era seccion
        }
    }
    
    //=========================BNF SENTENCIA====================================
    //NOTA:
    /*La BNF del docente genera ambiguedad con las secciones de ASIGNACION y 
    LLAMADA ya que de ambos su FIRST es ID
      Sentencia → Asignación | Llamada | Condicional | BucleFor | BucleWhile | 
                  BucleRepeat | Lectura | Impresión | Lambda */
     /**@param quitando la ambiguedad quedaria asi
      * sentencia --> sentencia1 masSentencia
      * masSentencia --> sentencia1 masSentencia | Lambda
      * sentencia1 --> asigancionLlamada | Llamada | Condicional | BucleFor | 
      *                 BucleWhile | BucleRepeat | Lectura | Impresión | Lambda
      * asigancionLlamada --> ID combinacion
      * combinacion --> asignacion | llamada */
    private void sentencia() {
        //Puede estar vacia
        sentencia1();
        masSentencia();
    }
    
    private void sentencia1() {
        switch (analex.preNom()) {
            case Token.ID:
                //asignacionLlamada(); // asignacionLlamada --> ID Combinacion
                asigancionLlamada();
                break;
            case Token.IF:
                condicional();
                break;
            case Token.FOR:
                bucleFor();
                break;
            case Token.WHILE:
                bucleWhile();
                break;
            case Token.REPEAT:
                bucleRepeat();
                break;
            case Token.READLN:
                lectura();
                break; 
            case Token.WRITELN:
                impresion();
                break; 
            default:
                ;
                break;
        }
    }
    
    private void masSentencia() {
        if (analex.preNom() == Token.ID || analex.preNom() == Token.IF ||
            analex.preNom() == Token.WRITELN || analex.preNom() == Token.FOR || 
            analex.preNom() == Token.WHILE || analex.preNom() == Token.REPEAT || 
            analex.preNom() == Token.READLN) {
            sentencia1();
            masSentencia();
        } //else 2da seccio lambda
    }
    
    private void sentenciaUnica() { //PARA EL WHILE, FOR, IF
        switch (analex.preNom()) {
            case Token.ID:
                //asignacionLlamada(); // asignacionLlamada --> ID Combinacion
                asigancionLlamada();
                break;
            case Token.READLN:
                lectura();
                break; 
            case Token.WRITELN:
                impresion();
                break; 
            default:
                ;
                break;
        }
    }
    
    //===========================BNF ASIGNACION-LLAMADA=========================
    
    private void asigancionLlamada() {
        match(Token.ID, "Se espera un ID");
        combinacion();
    }
    
    private void combinacion() {
        if (analex.preNom() == Token.ASSIGN) { //1era seccion
            asignacion();
        } else { //2da seccion
            llamada();
        }
    }
    
    //============================BNF ASINGNACION===============================
    
    private void asignacion() { //i := 5 + (2 + x);
        //match(Token.ID, "Se espera un ID");//hago macth en asignacionLlamada()
        match(Token.ASSIGN, "Falta :=");
        expr();
        match(Token.PTOCOMA, "Falta ;");
    }
    
    //==============================BNF LLAMADA=================================  
    
    private void llamada() {
        //match(Token.ID, "Se espera un ID"); //hago macth en asignacionLlamada()
        match(Token.PA, "Falta (");
        match(Token.PC, "Falta )");
        match(Token.PTOCOMA, "Falta ;");
    }  
    
    //=============================BNF CONDICIONAL==============================
    
    private void condicional() {
        match(Token.IF, "Se espera IF");      
        match(Token.PA, "Falta (");
        //System.out.println("holaaa");
        exprBool();
        match(Token.PC, "Falta )");
        match(Token.THEN, "Se espera THEN");
        condicional1(); 
    }
    
    private void condicional1() {
        if (analex.preNom() == Token.ID || analex.preNom() == Token.READLN
                || analex.preNom() == Token.WRITELN) { //1era seccion
            sentenciaUnica(); //Sentencia unica
            condicional2();
        } else {//2da seccion
            match(Token.BEGIN, "Se espera BEGIN");
            sentencia();
            match(Token.END, "Se espera END");
            condicional3();
            match(Token.PTOCOMA, "Falta ;");
        }
        
    }

    private void condicional2() {
        if (analex.preNom() == Token.ELSE) { //1era seccion 
            match(Token.ELSE, "Se espera ELSE");
            sentenciaUnica();
        } //else 2da seccion lambda
    }
       
    private void condicional3() {
        if (analex.preNom() == Token.ELSE) { //1era seccion 
            match(Token.ELSE, "Se espera ELSE");
            match(Token.BEGIN, "Se espera BEGIN");
            sentencia();
            match(Token.END, "Se espera END");
        } //else 2da seccion lambda
    }
    
    //===============================BNF BUCLEFOR===============================
    
    private void bucleFor(){
        match(Token.FOR, "Se espera FOR");
        match(Token.ID, "Se espera un ID");
        match(Token.ASSIGN, "Falta :=");
        expr();
        bucleFor1();
        expr();
        match(Token.DO, "Se espera DO");
        bucleFor2();
    }
    
    private void bucleFor1() {
        if (analex.preNom() == Token.TO)
            match(Token.TO, "Se espera TO"); //1era seccion
        else
            match(Token.DOWNTO, "Se espera DOWNTO"); //2da seccion
    }

    private void bucleFor2() {
        if (analex.preNom() == Token.ID || analex.preNom() == Token.READLN
                || analex.preNom() == Token.WRITELN) {//1era seccion
            sentenciaUnica();
        } else {//2da seccion
            match(Token.BEGIN, "Se espera BEGIN");
            sentencia();
            match(Token.END, "Se espera END");
            match(Token.PTOCOMA, "Falta ;");
        }
        
    }
    
    //==================================BNF BUCLEWHILE==========================
    
    private void bucleWhile(){
        match(Token.WHILE, "Se espera WHILE");
        match(Token.PA, "Falta (");
        exprBool();
        match(Token.PC, "Falta )");
        match(Token.DO, "Se espera DO");
        bucleWhile1();
    }
    
    private void bucleWhile1() {
        if (analex.preNom() == Token.ID || analex.preNom() == Token.READLN
                || analex.preNom() == Token.WRITELN) {//1era seccion
            sentenciaUnica();//unica sentencia
        } else {//2da seccion
            match(Token.BEGIN, "Se espera BEGIN");
            sentencia();
            match(Token.END, "Se espera END");
            match(Token.PTOCOMA, "Falta ;");
        }
    }
    
    //============================BNF BUCLEREPEAT===============================
    
    private void bucleRepeat() {
        match(Token.REPEAT, "Se espera REPEAT");
        sentencia();
        match(Token.UNTIL, "Se espera UNTIL");
        match(Token.PA, "Falta (");
        exprBool();
        match(Token.PC, "Falta )");
        match(Token.PTOCOMA, "Falta ;");
    } 
    
    //==============================BNF LECTURA=================================
    
    private void lectura() {
        match(Token.READLN, "Se espera READLN");
        match(Token.PA, "Falta (");
        match(Token.ID, "Se espera ID");
        masID();
        match(Token.PC, "Falta )");
        match(Token.PTOCOMA, "Falta ;");
    }
    
    //=============================BNF IMPRESION================================ 
    
    private void impresion() {
        match(Token.WRITELN, "Se espera un WRITELN");
        match(Token.PA, "Falta (");
        elem();
        masElem();
        match(Token.PC, "Falta )");
        match(Token.PTOCOMA, "Falta ;");
    }
    
    private void masElem() {
        if (analex.preNom() == Token.COMA) {
            match(Token.COMA, "Falta ,");
            elem();
            masElem();
        } //else 2da seccion lambda
    }
    
    private void elem() {
        if (analex.preNom() == Token.STRINGctte) //1era seccion
            match(Token.STRINGctte, "Se espera un STRINGCTTE");
        else //2da seccion
            expr();
    }
    
    //==================================BNF EXPR================================
    
    private void expr() {
        termino();
        expr2();
    }

    private void expr2() {
        if (analex.preNom() == Token.MAS || analex.preNom() == Token.MENOS) {
            expr1();
            expr2();
        } //else lambda 2da seccion
    }
    
    private void expr1() {
        if (analex.preNom() == Token.MAS) { //1era seccion
            match(Token.MAS, "Se espera +");
            termino();
        } else { //2da seccion
            match(Token.MENOS, "Se espera -");
            termino();
        }
    }
    
    private void termino() {
        factor();
        termino2();
    }

    private void termino2() {
        if (analex.preNom() == Token.POR || analex.preNom() == Token.DIV ||
            analex.preNom() == Token.MOD ) { //1era seccion
            termino1();
            termino2();
        } //else lambda 2da seccion
    }

    private void termino1() {
        switch (analex.preNom()) {
            case Token.POR: //1era seccion
                match(Token.POR, "Se espera *");
                factor();
                break;
            case Token.DIV: //2da seccion
                match(Token.DIV, "Se espera /");
                factor();
                break;
            default:  //3era seccion
                match(Token.MOD, "Se espera MOD");
                factor();
                break;
        }
    }

    private void factor() {
        switch (analex.preNom()) {
            case Token.ID: //1era seccion
                match(Token.ID, "Se espera un ID");
                break;
            case Token.NUM: //2da seccion
                match(Token.NUM, "Se espera un NUM");
                break;
            case Token.MENOS: //3era seccion
                match(Token.MENOS, "Se espera -");
                factor();
                break;
            case Token.MAS: //cuarta seccion
                match(Token.MAS, "Se espera +");
                factor();
                break;
            default:  //quinta seccion
                match(Token.PA, "Falta (");
                expr();
                match(Token.PC, "Falta )");
                break;
        }
    }
              
    //==============================BNF EXPRBOOL================================
    //NOTA:
     /**La BNF ExprBool del docente genera ambiguedad en la produccion de:
      *     FactorBool --> Expr OPREL Expr | (ExprBool) | NOT FactorBool
      * en la seccion Expr OPREL Expr y (ExprBool) ya que en ambas secciones 
      * su FIRST contienen "(" 
      * @param Decedi quitar la seccion (ExprBool) para quitar la ambiguedad
      La produccion FactorBool quedaria asi:
            FactorBool --> Expr OPREL Expr | NOT FactorBooL*/
    private void exprBool() {
        termBool();
        exprBool1();
    }

    private void exprBool1() {
        if (analex.preNom() == Token.OR) {//1era seccion
            match(Token.OR, "Se espera OR");
            termBool();
            exprBool1();
        } //else 2da seccion lambda
    }

    private void termBool() {
        factorBool();
        termBool1();
    }

    private void termBool1() {
        if (analex.preNom() == Token.AND) { //1era seccion
            match(Token.AND, "Se espera AND");
            factorBool();
            termBool1();
        } //ekse 2da seccion lambda
    }

    private void factorBool() {
        if (analex.preNom() == Token.NOT) { //2da seccion
            match(Token.NOT, "Se espera NOT");
            factorBool();
        } else { //1era seccion
            expr();
            match(Token.OPREL, "Se espera un  OPREL");
            expr();
        }
    }

    //==========================================================================       
    
}
