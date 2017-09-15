package CalculatorMain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Controller {

    @FXML
    public void titleBarPressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    @FXML
    public void titleBarDragged(MouseEvent event) {
        fxStage.setX(event.getScreenX() - mouseX);
        fxStage.setY(event.getScreenY() - mouseY);
    }

    @FXML
    public void minimize() {
        fxStage.setIconified(true);
    }

    @FXML
    public void close() {
        fxStage.close();
    }

    @FXML
    public void mouseEnteredClose() {
    }

    @FXML
    public void mouseExitedClose() {
    }


    @FXML
    TextArea output;
    @FXML
    Button memoryRecall;
    @FXML
    HBox titleBar;
    @FXML
    VBox mainWrapper;
    @FXML
    Stage fxStage;


    private double mouseX, mouseY;

    private String[] numberQueue;
    private String[] operatorQueue;
    private String memory;
    private int leftDecimalCount, rightDecimalCount, realOperatorQueueLength;
    private boolean leftNeg, rightNeg;



    // maybe move these methods up a little
    private String getEventType(ActionEvent event) {
        return ((Button) event.getSource()).getText();
    }

    private boolean isOperator(String string) {
        if (string != null && (string.equals("X") || string.equals("/") || string.equals("+") || string.equals("-"))) {
            return true;
        }
        return false;

    }

    private boolean isFunctionKey(String string) throws NumberFormatException{
        if (string.equals("DEL") || string.equals("+M") || string.equals("MR") || string.equals("AC") || string.equals("=")) {
            return true;
        }
        return false;
    }

    private boolean isInt(String string) throws NumberFormatException{
        if (Integer.parseInt(string) >= 0) {
            return true;
        }
        return false;
    }


    public void handleButtonClicked(ActionEvent event) {
        numberQueue = output.getText().split("[^0-9.]");
        operatorQueue = output.getText().split("[0-9.=]");

        // switch determines key press (function keys)
        switch (getEventType(event)) {
            case "AC":
                leftDecimalCount = rightDecimalCount = realOperatorQueueLength = 0;
                for (int i = 0; i < operatorQueue.length-1; i++){
                    operatorQueue[i] = "";
                }
                output.clear();
                break;
            case "+M":
                if (!output.getText().isEmpty()) {
                    memory = output.getText();
                }
                break;
            case "MR":
                if (!memory.isEmpty()) output.setText(memory);
                break;
            case "DEL":
                if (!output.getText().isEmpty())
                    output.deleteText(output.getLength() - 1, output.getLength());
                break;
            case ".":
                // do nothing special
                break;
            case "=":
                if (!output.getText().isEmpty()) {
                    leftDecimalCount = rightDecimalCount = realOperatorQueueLength = 0;
                    numberVerification("");
                }
                break;
        }


        // updated to use function key method
        if (!isFunctionKey(getEventType(event))) {
            if (getEventType(event).equals(".")) {
                if (realOperatorQueueLength == 0 && leftDecimalCount < 1) {
                    leftDecimalCount += 1;

                }
                if (realOperatorQueueLength > 0 && rightDecimalCount < 1) {
                    leftDecimalCount = 0;
                    rightDecimalCount += 1;
                }
            }
            System.out.println("left decimal count "+leftDecimalCount+", right decimal count "+rightDecimalCount);
            if (rightDecimalCount <= 1 && leftDecimalCount <= 1) {
                System.out.println("Appending here 1 ");
                output.appendText(getEventType(event));
            } else {
                if (!getEventType(event).equals(".")) {
                    System.out.println("Appending here 2 ");
                    output.appendText(getEventType(event));
                }
            }

        }





// Rolling from here ****************************

        if (operatorQueue.length >= 1){
            System.out.println("Operator Queue: "+Arrays.toString(operatorQueue));
        }
        if (isOperator(getEventType(event))){
            if (leftNeg) {
                System.out.println("Operator queue length >= 0");
                if (operatorQueue[0].equals("-") && operatorQueue.length <= 1) {
                    realOperatorQueueLength -= 1;
                }
            }

            String currOp = getEventType(event);
            realOperatorQueueLength+=1;
            if (realOperatorQueueLength >= 2){
                numberVerification(currOp);
            }
        }


// End rolling here  ****************************


    }


    private void numberVerification(String append) {

        if (append.equals("=")) append = "";


        // for rolling calculation
        /** Runs left side Check */
        if (operatorQueue.length >= 1) {
            if (operatorQueue[0].equals("-")) {
                leftNeg = true;
            } else {
                leftNeg = false;
            }


            /** RIGHT SIDE CHECK */
            if (operatorQueue.length >= 2) {
                // operator queues' length changes depending on number of inputs 6.23 - 6 --> [,,,-]
                if (operatorQueue[operatorQueue.length - 1].endsWith("-")) {
                    rightNeg = true;
                } else {
                    rightNeg = false;
                }
            }
            /** DOUBLE NEGATIVE CHECK*/
            if (leftNeg && rightNeg) {
                System.out.println("Both sides are negative");
                // if the last element of operator queue equals "-" (check for num - num)

                /** CHECK FOR SINGLE OPERATOR (GIVEN LEFT IS NEGATIVE) */
                if (operatorQueue[operatorQueue.length - 1].equals("-")) {
                    output.setText(calculate(Double.parseDouble(numberQueue[1]) * -1, Double.parseDouble(numberQueue[numberQueue.length - 1]) * -1, "+") + append);
                    // else (check for num x- num or num /- num and so on
                } else {
                    // this will not split (only split on element)
                    String[] negnegArrOpArray = operatorQueue[operatorQueue.length - 1].split("[-]");
                    // can get operator with operatorQueue[operatorQueue.length-1].startsWith
                    if (negnegArrOpArray[0].equals("+")) {
                        output.setText(calculate(Double.parseDouble(numberQueue[1]), Double.parseDouble(numberQueue[numberQueue.length - 1]), negnegArrOpArray[0]) * -1 + append);
                    } else
                        output.setText(calculate(Double.parseDouble(numberQueue[1]), Double.parseDouble(numberQueue[numberQueue.length - 1]), negnegArrOpArray[0]) + append);
                }
                /** LEFT NEGATIVE CHECK */
            } else if (leftNeg) {
                System.out.println("Left side is negative\nAnd here's the array info: "+Arrays.toString(operatorQueue));
                if (operatorQueue.length >= 2) {
                    output.setText((calculate(Double.parseDouble(numberQueue[1]) * -1, Double.parseDouble(numberQueue[2]), operatorQueue[operatorQueue.length - 1])) + append);
                }
                /** RIGHT NEGATIVE CHECK */
            } else if (rightNeg) {
                System.out.println("Right side is negative");
                /** TEMP REMOVAL OF LOGIC (rightOpBreakDown.length - 1 <= 1)*/
                /** CHECK FOR MULTI-OPERATOR (X-, /-)*/
                if (operatorQueue[operatorQueue.length - 1].length() <= 1) {
                    // this block is assuming that the right side only contains a negative sign, best conditional above
                    // if the right side has just a minus
                    output.setText((calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[numberQueue.length - 1]) * -1, "+")) + append);
                    /** ELSE THERE IS MULTIPLE OPERATORS (X-, /- IS PRESENT)*/
                } else {
                    // else if the right side has more than a minus (X-,/-)
                    String[] rightIsNegativeArrayNumberTwo = operatorQueue[operatorQueue.length - 1].split("[-]");
                    output.setText((calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[2]) * -1, rightIsNegativeArrayNumberTwo[0])) + append);
                }
                /** ELSE BOTH SIDES ARE POSITIVE */
            } else {
                System.out.println("Both sides are positive");
                output.setText(calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[1]), operatorQueue[operatorQueue.length - 1]) + append);
            }
        }
    }

    private double calculate(double left, double right, String operator) {
        System.out.println("##########################");
        System.out.println("Number Queue: " + Arrays.toString(numberQueue));
        System.out.println("Operator Queue: " + Arrays.toString(operatorQueue));
        System.out.println(left + ", " + right + " " + operator);
        System.out.println(" Real operator queue length " + realOperatorQueueLength);
        System.out.println("##########################");

        double answer;
        switch (operator) {
            case "+":
                answer = left + right;
                break;

            case "-":
                answer = left - right;
                break;

            case "X":
                answer = left * right;
                break;

            case "/":
                answer = left / right;
                break;
            // haven't added modulus (need room)
            case "%":
                answer = left % right;
                break;

            default:
                answer = -1;

        }
        return Double.parseDouble(String.format("%.5f", answer));

    }


}
