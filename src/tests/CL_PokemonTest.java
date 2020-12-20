package tests;

import api.GeoLocation;
import api.geo_location;
import gameClient.CL_Pokemon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CL_PokemonTest {
    static int POKEMON_SIZE = 2;
    static ArrayList<CL_Pokemon> pokemonsList;

    @BeforeEach
    void setUp() {

        Random rand = new Random();
        pokemonsList = new ArrayList<>();

//        for (int i = 0; i < POKEMON_SIZE; i++) {
//            geo_location pos = new GeoLocation(35.207151268054346,32.10259023385377,0);
//            CL_Pokemon pokemon = new CL_Pokemon(pos,-1,8,);
//            pokemonsList.add(pokemon);
//        }
    }

    @Test
    void init_from_json() {

    }

    @Test
    void getEdge() {
    }

    @Test
    void getLocation() {
        for (int i = 0; i < POKEMON_SIZE; i++) {
            geo_location p = pokemonsList.get(i).getLocation();
            assertEquals(p.toString(), pokemonsList.get(i).getLocation().toString());
        }
    }

    @Test
    void getType() {
        for (int i = 0; i < POKEMON_SIZE; i++) {
            assertEquals(-1, pokemonsList.get(i).getType());
        }
    }

    @Test
    void getValue() {
        for (int i = 0; i < POKEMON_SIZE - 1; i++) {
            pokemonsList.get(i).setValue(i);
            double val = pokemonsList.get(i).getValue();
            assertEquals(val, i);
        }
    }

    @Test
    void getMinDist() {
    }

    @Test
    void setMinDist() {
    }

    @Test
    void getMinRo() {
    }

    @Test
    void setMinRo() {
    }
}