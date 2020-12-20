package gameClient;

import api.edge_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * This class represents the pokemon in the game.
 * Each pokemon has:
 * value - which represents the pokemon's value.
 * edge - which represents the current pokemon's edge.
 * type - which represents the pokemon's type.
 * If the type is 1: the pokemon is on the edge from  the lesser node to the greater node,
 * (according to the ID's value of the node).
 * If the type is -1: the pokemon is on the edge from  the greater node to the lesser node.
 * pos - a 3D point that represents the position of the agent in the graph.
 * currNode - which represents the current agent's node.
 * id - which represents the pokemon's ID, which combines its coordinates.
 */
public class CL_Pokemon {
	private edge_data edge;
	private double value;
	private int type;
	private Point3D pos;
	private double minDist;
	private int minRo;
	private String id;
	
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		type = t;
	//	_speed = s;
		value = v;
		setEdge(e);
		pos = p;
		minDist = -1;
		minRo = -1;
		id = Double.toString(this.getLocation().x() + this.getLocation().y());
	}

	/*HELPFUL TOOLS*/
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	public String toString() {return "Pokemon details:{id="+ id + ", v="+ value +", t="+ type +"}";}

	/*GETS & SETS*/
	public String get_id() {
		return id;
	}

	public edge_data getEdge() {
		return edge;
	}

	public void setEdge(edge_data edge) {
		this.edge = edge;
	}

	public Point3D getLocation() {
		return pos;
	}

	public int getType() {return type;}

//	public double getSpeed() {return _speed;}

	public double getValue() {return value;}

	public void setValue(double value) {
		this.value = value;
	}

	public double getMinDist() {
		return minDist;
	}

	public void setMinDist(double mid_dist) {
		this.minDist = mid_dist;
	}

	public int getMinRo() {
		return minRo;
	}

	public void setMinRo(int minRo) {
		this.minRo = minRo;
	}
}
