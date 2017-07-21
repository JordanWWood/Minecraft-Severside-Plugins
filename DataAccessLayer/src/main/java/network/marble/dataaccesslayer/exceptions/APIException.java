package network.marble.dataaccesslayer.exceptions;

public class APIException extends Exception {
	private static final long serialVersionUID = 5151589007970795509L;
	public String code;

    public APIException(String code, String message) {
        super(message);
        this.code = code;
    }
}
