package Parsing;

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
        
    }
    
    private void main(){
        match(Token.BEGIN, "Se espera BEGIN");
        match(Token.END, "Se espera END");
        match(Token.PTO, "Se espera un .");
    }
    
    private void match(int tokenNom, String errorMsj){
        if (analex.preNom() == tokenNom)
            analex.avanzar();
        else
            errorMgr.setError(errorMsj, analex.getPosLexema(), analex.lexema());
    }
}
