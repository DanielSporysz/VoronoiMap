package IO;

import javafx.util.Pair;
import terrain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerrainFile {

    private List<ErrorLog> errorLog = new ArrayList<>();

    public List<ErrorLog> getErrorLog() {
        return errorLog;
    }

    private boolean isByte(String stringToCheck) {
        try {
            Byte.parseByte(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isShort(String stringToCheck) {
        try {
            Short.parseShort(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInteger(String stringToCheck) {
        try {
            Integer.parseInt(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLong(String stringToCheck) {
        try {
            Long.parseLong(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFloat(String stringToCheck) {
        try {
            Float.parseFloat(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDouble(String stringToCheck) {
        try {
            Double.parseDouble(stringToCheck);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBoolean(String stringToCheck) {
        return stringToCheck.matches("True|False");
    }

    private boolean isChar(String stringToCheck) {
        return stringToCheck.length() == 1;
    }

    public TerrainData readFile(File fileToRead) {
        TerrainData terrainData = new TerrainData();
        List<TerrainPoint> points = new ArrayList<>();
        List<TerrainObject> majorObjects = new ArrayList<>();
        Map<String, TerrainTemplate> templateObjects = new HashMap<>();
        List<TerrainObject> objects = new ArrayList<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), StandardCharsets.UTF_8));
            int hashCount = 0;
            int lineNumber = 0;
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                currentLine = currentLine.trim();
                currentLine = currentLine.replace(",", ".");
                lineNumber++;

                if (currentLine.length() > 0 && (currentLine.contains("#"))) {
                    hashCount++;
                } else {
                    String[] splittedLine = currentLine.split("[ ]+");
                    if (hashCount == 1) {
                        if (splittedLine.length >= 3) {
                            if (splittedLine[0].matches("[0-9]+[.]?") && isDouble(splittedLine[1]) && isDouble(splittedLine[2])) {
                                if (splittedLine.length > 3) {
                                    errorLog.add(new ErrorLog(2, lineNumber, currentLine));
                                }
                                points.add(new TerrainPoint(Double.parseDouble(splittedLine[1]), Double.parseDouble(splittedLine[2])));
                            } else {
                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                            }
                        } else {
                            if (!currentLine.isEmpty()) {
                                errorLog.add(new ErrorLog(0, lineNumber, currentLine));
                            }
                        }
                    } else if (hashCount == 2) {
                        if (splittedLine.length >= 4) {
                            if (splittedLine[0].matches("[0-9]+[.]?") && isDouble(splittedLine[1]) && isDouble(splittedLine[2])) {
                                StringBuilder objectName = new StringBuilder(splittedLine[3]).append(" ");
                                if (splittedLine.length > 4) {
                                    for (int i = 4; i < splittedLine.length; i++) {
                                        objectName.append(splittedLine[i]).append(" ");
                                    }
                                }
                                String strObjectName = objectName.toString();
                                TerrainPoint point = new TerrainPoint(Double.parseDouble(splittedLine[1]), Double.parseDouble(splittedLine[2]));
                                TerrainObject terrainObject = new TerrainObject(strObjectName, point);
                                majorObjects.add(terrainObject);
                            } else {
                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                            }
                        } else {
                            if (!currentLine.isEmpty()) {
                                errorLog.add(new ErrorLog(0, lineNumber, currentLine));
                            }
                        }
                    } else if (hashCount == 3) {
                        if (splittedLine.length >= 2) {
                            if (splittedLine[0].matches("[0-9]+[.]?")) {
                                TerrainTemplate terrainTemplate = new TerrainTemplate(splittedLine[1]);
                                for (int i = 2; i < splittedLine.length; i += 2) {
                                    String name = splittedLine[i];
                                    String type;
                                    if (i + 1 != splittedLine.length) {
                                        type = splittedLine[i + 1];
                                        if (type.matches("byte|short|int|long|float|double|boolean|char|string|Byte|Short|Int|Long|Float|Double|Boolean|Char|String")) {
                                            terrainTemplate.getVariablesInside().add(new Pair<>(name, type));
                                        } else {
                                            errorLog.add(new ErrorLog(5, lineNumber, currentLine));
                                        }
                                    } else {
                                        errorLog.add(new ErrorLog(4, lineNumber, currentLine));
                                    }
                                }
                                if (!templateObjects.containsKey(splittedLine[1])) {
                                    templateObjects.put(splittedLine[1], terrainTemplate);
                                } else {
                                    errorLog.add(new ErrorLog(3, lineNumber, currentLine));
                                }
                            }
                        } else {
                            if (!currentLine.isEmpty()) {
                                errorLog.add(new ErrorLog(0, lineNumber, currentLine));
                            }
                        }
                    } else if (hashCount == 4) {
                        if (splittedLine.length >= 2) {
                            if (splittedLine[0].matches("[0-9]+[.]?")) {
                                if (templateObjects.containsKey(splittedLine[1])) {
                                    TerrainTemplate terrainTemplate = templateObjects.get(splittedLine[1]);
                                    ArrayList<Pair<String, String>> variablesInside = terrainTemplate.getVariablesInside();
                                    TerrainObject terrainObject = new TerrainObject(splittedLine[1], new TerrainPoint(0, 0));
                                    double X = Double.MAX_VALUE, Y = Double.MAX_VALUE;
                                    boolean xRead = false, yRead = false;
                                    int k = 0;
                                    for (int i = 2; i < splittedLine.length; i++) {
                                        if (k >= variablesInside.size()) {
                                            errorLog.add(new ErrorLog(2, lineNumber, currentLine));
                                            break;
                                        }
                                        String name = variablesInside.get(k).getKey();
                                        String type = variablesInside.get(k).getValue();
                                        StringBuilder value = new StringBuilder();
                                        if (type.matches("String|string")) {
                                            value.append(splittedLine[i]).append(" ");
                                            while (splittedLine[i].charAt(splittedLine[i].length() - 1) != 34 && i + 1 < splittedLine.length) {
                                                i++;
                                                value.append(splittedLine[i]).append(" ");
                                            }
                                            terrainObject.getStringMap().put(name, value.toString());
                                        } else if (type.matches("Byte|byte")) {
                                            if (isByte(splittedLine[i])) {
                                                terrainObject.getByteMap().put(name, Byte.parseByte(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Short|short")) {
                                            if (isShort(splittedLine[i])) {
                                                terrainObject.getShortMap().put(name, Short.parseShort(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Int|int")) {
                                            if (isInteger(splittedLine[i])) {
                                                terrainObject.getIntegerMap().put(name, Integer.parseInt(splittedLine[i]));
                                                if (name.equals("L_MIESZKAŃCÓW")) {
                                                    terrainObject.setIsInhibitiant(true);
                                                    terrainObject.setInhibitiants(Integer.parseInt(splittedLine[i]));
                                                }
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Long|long")) {
                                            if (isLong(splittedLine[i])) {
                                                terrainObject.getLongMap().put(name, Long.parseLong(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Float|float")) {
                                            if (isFloat(splittedLine[i])) {
                                                terrainObject.getFloatMap().put(name, Float.parseFloat(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Double|double")) {
                                            if (isDouble(splittedLine[i])) {
                                                if (name.equals("X")) {
                                                    X = Double.parseDouble(splittedLine[i]);
                                                    xRead = true;
                                                }
                                                if (name.equals("Y")) {
                                                    Y = Double.parseDouble(splittedLine[i]);
                                                    yRead = true;
                                                }
                                                if (xRead && yRead) {
                                                    TerrainPoint terrainPoint = new TerrainPoint(X, Y);
                                                    terrainObject.setPosition(terrainPoint);
                                                }
                                                terrainObject.getDoubleMap().put(name, Double.parseDouble(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Bool|bool")) {
                                            if (isBoolean(splittedLine[i])) {
                                                terrainObject.getBooleanMap().put(name, Boolean.parseBoolean(splittedLine[i]));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        } else if (type.matches("Char|char")) {
                                            if (isChar(splittedLine[i])) {
                                                terrainObject.getCharacterMap().put(name, splittedLine[i].charAt(0));
                                            } else {
                                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                                                break;
                                            }
                                        }
                                        k++;
                                    }
                                    objects.add(terrainObject);
                                } else {
                                    errorLog.add(new ErrorLog(6, lineNumber, currentLine));
                                }
                            } else {
                                errorLog.add(new ErrorLog(1, lineNumber, currentLine));
                            }
                        } else {
                            if (!currentLine.isEmpty()) {
                                errorLog.add(new ErrorLog(0, lineNumber, currentLine));
                            }
                        }
                    }
                }
            }
            TerrainContour terrainContour = new TerrainContour(points);
            terrainData.setContour(terrainContour);
            terrainData.setMajorObjects(majorObjects);
            terrainData.setTemplateObject(templateObjects);
            terrainData.setObjects(objects);
            return terrainData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(File fileToSave, TerrainData terrainData) {
        TerrainContour terrainContour = terrainData.getContour();
        List<TerrainObject> majorObjects = terrainData.getMajorObjects();
        Map<String, TerrainTemplate> templateObjects = terrainData.getTemplateObject();
        List<TerrainObject> objects = terrainData.getObjects();

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8);
            writer.write("#\r\n");
            int i = 1;
            for (TerrainPoint x : terrainContour.getPoints()) {
                writer.write(i + " " + x.x + " " + x.y + "\r\n");
                i++;
            }
            i = 1;
            writer.write("\r\n#\r\n");
            for (TerrainObject x : majorObjects) {
                writer.write(i + " " + x.getPosition().x + " " + x.getPosition().y + " " + x.getType() + "\r\n");
                i++;
            }
            i = 1;
            writer.write("\r\n#\r\n");
            for (String name : templateObjects.keySet()) {
                writer.write(i + " " + name + " ");
                for (Pair<String, String> x : templateObjects.get(name).getVariablesInside()) {
                    writer.write(x.getKey() + " " + x.getValue() + " ");
                }
                i++;
                writer.write("\r\n");
            }
            i = 1;
            writer.write("\r\n#\r\n");
            for (TerrainObject x : objects) {
                writer.write(i + " " + x.getType() + " ");
                for (Pair<String, String> y : templateObjects.get(x.getType()).getVariablesInside()) {
                    String variableType = y.getValue();
                    String variableName = y.getKey();
                    if (variableType.matches("Byte|byte")) {
                        writer.write(x.getByteMap().get(variableName) + " ");
                    } else if (variableType.matches("Short|short")) {
                        writer.write(x.getShortMap().get(variableName) + " ");
                    } else if (variableType.matches("Int|int")) {
                        writer.write(x.getIntegerMap().get(variableName) + " ");
                    } else if (variableType.matches("Long|long")) {
                        writer.write(x.getLongMap().get(variableName) + " ");
                    } else if (variableType.matches("Float|float")) {
                        writer.write(x.getFloatMap().get(variableName) + " ");
                    } else if (variableType.matches("Double|double")) {
                        writer.write(x.getDoubleMap().get(variableName) + " ");
                    } else if (variableType.matches("Bool|bool")) {
                        writer.write(x.getBooleanMap().get(variableName) + " ");
                    } else if (variableType.matches("Char|char")) {
                        writer.write(x.getCharacterMap().get(variableName) + " ");
                    } else if (variableType.matches("String|string")) {
                        writer.write(x.getStringMap().get(variableName) + " ");
                    }
                }
                i++;
                writer.write("\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
