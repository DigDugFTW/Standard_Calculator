package CalculatorMain;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


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
    public void keyPressed(KeyEvent keyEvent) {
        handleButtonClicked(keyEvent);
    }


    @FXML
    TextArea output;
    @FXML
    HBox titleBar;
    @FXML
    Stage fxStage;


    private double mouseX, mouseY;

    private String[] numberQueue;
    private String[] operatorQueue;
    private String memory;
    private int leftDecimalCount, rightDecimalCount, realOperatorQueueLength;
    private boolean leftNeg, rightNeg;


    // maybe move these methods up a little


    /**
     * @param event Pass button event as parameter to get button text
     * @return the text location on the button
     */

    private String getEventType(Event event) {
        if (!event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
            return (((Button) event.getSource()).getText());
        } else if (((KeyEvent) event).getCode().isKeypadKey() || isOperator(((KeyEvent) event).getText()) || ((KeyEvent) event).getText().equals(".")) {
            return ((KeyEvent) event).getText();
        } else if (((KeyEvent) event).getCode().equals(KeyCode.ENTER)) {
            return "=";
        } else if (((KeyEvent) event).getCode().equals(KeyCode.MULTIPLY)) {
            return "X";
        } else if (((KeyEvent) event).getCode().equals(KeyCode.BACK_SPACE)) {
            return "DEL";
        } else if (((KeyEvent) event).isShiftDown()) {
            return "AC";
        } else
            return "";
    }

    private boolean isOperator(String string) {
  return string != null && (string.equals("X") || string.equals("/") || string.equals("+") || string.equals("-")
          || string.equals("ADD") || string.equals("MULTIPLY") || string.equals("SUBTRACT") || string.equals("DIVIDE"));


    }


    private boolean isFunctionKey(String string) throws NumberFormatException {
       return string.equals("DEL") || string.equals("+M") || string.equals("MR") || string.equals("AC") || string.equals("=") || string.equals("ENTER") || string.equals("x^2") || string.equals("x^3");
    }

    @SuppressWarnings("WeakerAccess")
    public void handleButtonClicked(Event event) {
        numberQueue = output.getText().split("[^0-9.]");
        operatorQueue = output.getText().split("[0-9.=]");

        // switch determines key press (function keys)
        String tmp_output = output.getText();
        switch (getEventType(event)) {

            case "AC":
                leftDecimalCount = rightDecimalCount = realOperatorQueueLength = 0;
                for (int i = 0; i < operatorQueue.length - 1; i++) {
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
            case "x^2":
                // do nothing special
                output.clear();
                output.appendText(Math.pow(Double.parseDouble(tmp_output), 2) + "");
                break;
            case "x^3":
                output.clear();
                output.appendText(Math.pow(Double.parseDouble(tmp_output), 3) + "");
                break;
            case "=":
                if (!output.getText().isEmpty()) {
                    leftDecimalCount = rightDecimalCount = realOperatorQueueLength = 0;
                    numberVerification("");
                }
                break;
            case "ENTER":
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
            if (rightDecimalCount <= 1 && leftDecimalCount <= 1) {
                output.appendText(getEventType(event));
            } else {
                if (!getEventType(event).equals(".")) {
                    output.appendText(getEventType(event));
                }
            }

        }


// Rolling from here ****************************

        if (operatorQueue.length >= 1) {
            System.out.println("Operator queue is greater or equal to 1");
        }
        if (isOperator(getEventType(event))) {
            if (leftNeg) {
                System.out.println("Operator queue length >= 0");
                if (operatorQueue[0].equals("-") && operatorQueue.length <= 1) {
                    realOperatorQueueLength -= 1;
                }
            }
            String currOp = getEventType(event);
            realOperatorQueueLength += 1;
            if (realOperatorQueueLength >= 2) {
                numberVerification(currOp);
            }
        }


// End rolling here  ****************************


    }

    /**
     * @param append What is appended after calculation
     *               ex: 1+2+ would result in 3+ for the next calculation
     */
    private void numberVerification(String append) {

        if (append.equals("=") || append.equals("x^2") || append.equals("x^3")) append = "";


        // for rolling calculation
        /* Runs left side Check */
        if (operatorQueue.length >= 1) {
            if (operatorQueue[0].equals("-")) {
                leftNeg = true;
            } else {
                leftNeg = false;
            }




            /* RIGHT SIDE CHECK */
            if (operatorQueue.length >= 2) {
                // operator queues' length changes depending on number of inputs 6.23 - 6 --> [,,,-]
                if (operatorQueue[operatorQueue.length - 1].endsWith("-")) {
                    rightNeg = true;
                } else {
                    rightNeg = false;
                }
            }
            /* DOUBLE NEGATIVE CHECK*/
            if (leftNeg && rightNeg) {
                // if the last element of operator queue equals "-" (check for num - num)

                /* CHECK FOR SINGLE OPERATOR (GIVEN LEFT IS NEGATIVE) */
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
                /* LEFT NEGATIVE CHECK */
            } else if (leftNeg) {
                if (operatorQueue.length >= 2) {
                    output.setText((calculate(Double.parseDouble(numberQueue[1]) * -1, Double.parseDouble(numberQueue[2]), operatorQueue[operatorQueue.length - 1])) + append);
                }
                /* RIGHT NEGATIVE CHECK */
            } else if (rightNeg) {
                /* TEMP REMOVAL OF LOGIC (rightOpBreakDown.length - 1 <= 1)*/
                /* CHECK FOR MULTI-OPERATOR (X-, /-)*/
                if (operatorQueue[operatorQueue.length - 1].length() <= 1) {
                    // this block is assuming that the right side only contains a negative sign, best conditional above
                    // if the right side has just a minus
                    output.setText((calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[numberQueue.length - 1]) * -1, "+")) + append);
                    /* ELSE THERE IS MULTIPLE OPERATORS (X-, /- IS PRESENT)*/
                } else {
                    // else if the right side has more than a minus (X-,/-)
                    String[] rightIsNegativeArrayNumberTwo = operatorQueue[operatorQueue.length - 1].split("[-]");
                    output.setText((calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[2]) * -1, rightIsNegativeArrayNumberTwo[0])) + append);
                }
                /* ELSE BOTH SIDES ARE POSITIVE */
            } else {
                output.setText(calculate(Double.parseDouble(numberQueue[0]), Double.parseDouble(numberQueue[1]), operatorQueue[operatorQueue.length - 1]) + append);
            }
        }
    }

    private double calculate(double left, double right, String operator) {
        double answer = 0;
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
            case "%":
                answer = left % right;
                break;
            case "^":
                answer = Math.pow(left, right);
                break;
            case "E":
                // may process numbers in scientific notation x+(y * 10^substring("E"))
                break;
            default:
                answer = -1;

        }
        return Double.parseDouble(String.format("%.5f", answer));
    }
}
