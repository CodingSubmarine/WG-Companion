package braess.constantin.wgterminal;

public class Chore {
    String name;
    Roommate turn;
    int priority = 0;

    public Chore(String name, Roommate turn, int priority){
        this.name = name;
        this.turn = turn;
        this.priority = priority;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Roommate getTurn() {
        return turn;
    }

    public void setTurn(Roommate turn) {
        this.turn = turn;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void moveOn(){
        if (turn == Roommate.CONSTANTIN) {
            turn = Roommate.JAN;
            return;
        }
        if (turn == Roommate.JAN) {
            turn = Roommate.MARC;
            return;
        }
        if (turn == Roommate.MARC) {
            turn = Roommate.CONSTANTIN;
            return;
        }
    }
}

enum Roommate {
    JAN, MARC, CONSTANTIN;
}


