package IO;

public class ErrorLog {
    private int errorCode;
    private int lineNumber;
    private String line;

    ErrorLog(int errorCode, int lineNumber, String line) {
        this.errorCode = errorCode;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (errorCode) {
            case 0:
                sb.append("Błąd: Zbyt mało argumentów w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 1:
                sb.append("Błąd: Nieprawidłowe argumenty w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 2:
                sb.append("Ostrzeżenie: Zbyt dużo argumentów w linii: ").append(lineNumber).append(" Nadmiarowe argumenty zostały pominięte.").append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 3:
                sb.append("Błąd: W programie istnieje o takiej nazwie jak w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 4:
                sb.append("Błąd: Niepełna deklaracja szablonu w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 5:
                sb.append("Błąd: Niewłaściwy typ argumentu w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
            case 6:
                sb.append("Błąd: Niewłaściwy typ obiektu w linii: ").append(lineNumber).append("\r\n\t").append(line).append("\r\n");
                return sb.toString();
        }
        return null;
    }
}
