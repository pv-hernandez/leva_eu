package lp3.levaeu.application;
import android.os.Parcel;
import android.os.Parcelable;


public class Veiculo implements Parcelable {

	private int id;
	private String placa;
	private int renavam;
	private int capacidade;
	
	public Veiculo(int id, String placa, int renavam, int capacidade) {
		this.id = id;
		this.placa = placa;
		this.renavam = renavam;
		this.capacidade = capacidade;
	}
	
	private Veiculo(Parcel in) {
		this.id = in.readInt();
		this.placa = in.readString();
		this.renavam = in.readInt();
		this.capacidade = in.readInt();
	}

	public int getId() {
		return this.id;
	}
	
	public String getPlaca() {
		return this.placa;
	}
	
	public int getRenavam() {
		return this.renavam;
	}
	
	public int getCapacidade() {
		return this.capacidade;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(placa);
		out.writeInt(renavam);
		out.writeInt(capacidade);
	}
	
	public static final Parcelable.Creator<Veiculo> CREATOR = new Parcelable.Creator<Veiculo>() {
        public Veiculo createFromParcel(Parcel in) {
            return new Veiculo(in); 
        }

        public Veiculo[] newArray(int size) {
            return new Veiculo[size];
        }
    };
}
