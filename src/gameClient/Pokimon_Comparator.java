package gameClient;

import java.util.Comparator;

public class Pokimon_Comparator implements Comparator<CL_Pokemon> {

    @Override
    public int compare(CL_Pokemon p1, CL_Pokemon p2){
        return (int) (p1.getValue()-p2.getValue());
    }
}

