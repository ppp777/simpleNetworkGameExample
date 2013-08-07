import javax.swing.*;
import java.io.Serializable;

public class GameNetPacket implements Serializable{
    private JButton button;
    private String dataString;
    GameNetPacket(JButton button, String dataString){
        this.button = button;
        this.dataString = dataString;
    }

    public JButton getCode() {
        return button;
    }

    public void setCode(JButton button) {
        this.button = button;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }
    public String toString(){
        return " >> " + getCode() + "; msg = " + getDataString();
    }
}
