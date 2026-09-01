public class Transaction {
    int id ;
    String type ;
    Double amount ;
    Transaction (int id , String type , double amount){
        this.id=id ;
        this.type=type ;
        this.amount=amount ;
    }
    public void display (){
        System.out.println("id= "+id);
        System.out.println("type= "+type);
        System.out.println("amount= "+amount);
    }


}

