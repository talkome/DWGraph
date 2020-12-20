package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the agents in the game.
 * The agent rule is to collect all the pokemon in the game.
 * Each agent has:
 * speed - which represents the speed of the agent.
 * The speed is changed according to the number of pokemon who eats, the greater the value, the greater the speed.
 * pos - which represents the position of the agent in the graph.
 * currEdge - which represents the current agent's edge.
 * currNode - which represents the current agent's node.
 * value - which represents the agent's value, which sums the value of tall the pokemon that the agent ate.
 * targetPokemonsList - which contains the ID of all the pokemon  that the agent is chasing.
 */
public class CL_Agent {
	public static final double EPS = 0.0001;
	private static int _count = 0;
	private static int _seed = 3331;
	private int id;
	//	private long _key;
	private geo_location pos;
	private double speed;
	private edge_data currEdge;
	private node_data currNode;
	private directed_weighted_graph graph;
	private CL_Pokemon currPokemon;
	private long _sg_dt;
	private double value;
	private List<String> targetPokemonsList;
	private String pic = "resources/agent.png";
	private boolean _chasing;

	// Constructor
	public CL_Agent(directed_weighted_graph g, int start_node) {
		graph = g;
		setMoney(0);
		this.currNode = graph.getNode(start_node);
		pos = currNode.getLocation();
		id = -1;
		setSpeed(0);
		_chasing = false;
		this.targetPokemonsList = new ArrayList<>();
	}

	/*HELPFUL TOOLS*/

	// Update the agent's target list
	public void updateTargetPokemonsList(CL_Pokemon pokemon) {
		List<String> tempTargetPokemonsList = new ArrayList<>();
		for (String currPokemon : getTargetPokemonsList()) {
			tempTargetPokemonsList.add(currPokemon);
		}
		tempTargetPokemonsList.add(pokemon.get_id());
		this.setTargetPokemonsList(tempTargetPokemonsList);
	}

	// Return if the agent is moving
	public boolean isMoving() {
		return this.currEdge != null;
	}

	// Set the agent's next node
	public boolean setNextNode(int dest) {
		boolean ans = false;
		int src = this.currNode.getKey();
		this.currEdge = graph.getEdge(src, dest);
		if(currEdge != null)
			ans = true;
		else
			currEdge = null;
		return ans;
	}

	// Update
	public void update(String json) {
			JSONObject line;
			try {
				// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
				line = new JSONObject(json);
				JSONObject ttt = line.getJSONObject("Agent");
				int id = ttt.getInt("id");
				if(id==this.getID() || this.getID() == -1) {
					if(this.getID() == -1) {
						this.id = id;}
					double speed = ttt.getDouble("speed");
					String p = ttt.getString("pos");
					Point3D pp = new Point3D(p);
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					double value = ttt.getDouble("value");
					this.pos = pp;
					this.setCurrNode(src);
					this.setSpeed(speed);
					this.setNextNode(dest);
					this.setMoney(value);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		// Convert the agent data to Json format
	public String toJSON() {
		int d = this.getNextNode();
		return "{\"Agent\":{"
				+ "\"id\":"+ this.id +","
				+ "\"value\":"+ this.value +","
				+ "\"src\":"+ this.currNode.getKey()+","
				+ "\"dest\":"+d+","
				+ "\"speed\":"+ this.getSpeed()+","
				+ "\"pos\":\""+ pos.toString()+"\""
				+ "}"
				+ "}";
	}

	// ToString
	public String toString() {
		return toJSON();
	}

	public String toString1() {
		return ""+ this.getID()+","+ pos +", "+isMoving()+","+ this.getValue();
	}

	/*Getters & Setters*/
	public List<String> getTargetPokemonsList() {
		return targetPokemonsList;
	}

	// Set the agent's target list
	public void setTargetPokemonsList(List<String> targetPokemonsList) {
		this.targetPokemonsList = targetPokemonsList;
	}

	// Clear the agent's list
	public void clearAgentTargetList() {
		this.targetPokemonsList .clear();
	}

	// Get the agent's source node
	public int getSrcNode() {return this.currNode.getKey();}

	// Set the agent's money
	private void setMoney(double v) {
		value = v;
	}

	// Set the agent's current node
	public void setCurrNode(int src) {
		this.currNode = graph.getNode(src);
	}

	// Get the agent's source node
	public int getID() {
		return this.id;
	}

	// Get the agent's location
	public geo_location getLocation() {
		return pos;
	}

	// Get the agent's pic
	public String getPic() {
		return pic;
	}

	// Get the agent's value
	public double getValue() {
		return this.value;
	}

	// Get the agent's net node
	public int getNextNode() {
		int ans = -2;
		if(currEdge == null)
			ans = -1;
		else
			ans = this.currEdge.getDest();
		return ans;
	}

	// Get the agent's speed
	public double getSpeed() {
		return this.speed;
	}

	// Set the agent's source node
	public void setSpeed(double v) {
		this.speed = v;
	}

	// Get the agent's current pokemon
	public CL_Pokemon getCurrPokemon() {
		return currPokemon;
	}

	// set the agent's current pokemon
	public void setCurrPokemon(CL_Pokemon currPokemon) {
		this.currPokemon = currPokemon;
	}

	// Set the agent's SDT
	public void set_SDT(long ddtt) {
			long ddt = ddtt;
			if(this.currEdge !=null) {
				double w = getCurrEdge().getWeight();
				geo_location dest = graph.getNode(getCurrEdge().getDest()).getLocation();
				geo_location src = graph.getNode(getCurrEdge().getSrc()).getLocation();
				double de = src.distance(dest);
				double dist = pos.distance(dest);
				if(this.getCurrPokemon().getEdge() == this.getCurrEdge()) {
					 dist = currPokemon.getLocation().distance(this.pos);
				}
				double norm = dist/de;
				double dt = w*norm / this.getSpeed(); 
				ddt = (long)(1000.0*dt);
			}
			this.set_sg_dt(ddt);
		}

	// Get the agent's current edge
	public edge_data getCurrEdge() {
		return this.currEdge;
	}

	// Get the agent's sg_dt
	public long get_sg_dt() {
		return _sg_dt;
	}

	// Set the agent's sg_dt
	public void set_sg_dt(long _sg_dt) {
		this._sg_dt = _sg_dt;
	}
}
