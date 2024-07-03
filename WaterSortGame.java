import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class WaterSortGame {
    private StackAsMyArrayList<ColorBottle>[] bottles;
    public int maxBottleSize;
    StackAsMyArrayList<ColorBottle> selectedBottle;
    int selectedBottleNumber = -1;
    private boolean emptyBottleAdded = false;
    private StackAsMyArrayList<UndoAction> undoStack;
    private StackAsMyArrayList<RedoAction> redoStack;
    StackAsMyArrayList saveSelect = new StackAsMyArrayList<>();
    private int undoCount,redoCount;
    StackAsMyArrayList<ColorBottle> emptyBottleStack;
    public WaterSortGame(String[] colors, int maxBottleSize) {
        this.bottles = new StackAsMyArrayList[colors.length + 1];
        this.maxBottleSize = maxBottleSize;

        undoStack = new StackAsMyArrayList<>();
        redoStack = new StackAsMyArrayList<>();
        undoCount = 0;
        redoCount = 0;
        for (int i = 0; i < colors.length; i++) {
            bottles[i] = new StackAsMyArrayList<>();
        }
        bottles[colors.length] = new StackAsMyArrayList<>();
        fill(colors);
    }
    private void fill(String[] colors) {
        ArrayList<String> colorList = new ArrayList<>();
        Collections.addAll(colorList, colors);
        Collections.shuffle(colorList);// Shuffle the color list to get random order
        // Fill bottles with random colors
        int colorIndex = 0;
        for (int i = 0; i <= bottles.length ; i++) {
            if(i == bottles.length){
                    ColorBottle lastBottle = new ColorBottle("Empty");
                    bottles[i].push(lastBottle);
                    bottles[i].clear();
            }
            if (i == bottles.length - 1) {
                // The last bottle is initially empty
                ColorBottle lastBottle = new ColorBottle("Empty");
                bottles[i].push(lastBottle);
                bottles[i].clear();
                break;
            }
            for (int j = 0; j < maxBottleSize; j++) {
                String color = colorList.get(colorIndex % colorList.size());
                bottles[i].push(new ColorBottle(color));
                colorIndex++;
            }
        }
    }
    public void display() {
        System.out.println(selectedBottleNumber);
        int maxHeight = 0;
        for (StackAsMyArrayList<ColorBottle> bottleStack : bottles) {
            if (bottleStack != null) {
                maxHeight = Math.max(maxHeight, bottleStack.getStackSize());
            }
        }
        int index = 1;
        // Display the bottles
        for (int row = maxHeight - 1; row >= 0; row--) {
            index = 1;
            for (int col = 0; col < bottles.length; col++) {
                StackAsMyArrayList<ColorBottle> bottleStack = bottles[col];
                if (bottleStack != null && row < bottleStack.getStackSize()) {
                    ColorBottle bottle = bottleStack.get(row);
                    // Use String.format for ordered printing
                    String format = "%-10s"; // Adjust the width as needed
                    System.out.print(String.format(format, index + bottle.getColor()));
                } else {
                    System.out.print(String.format("%-10s", "Empty"));
                }
                // Add separator only at the end of the last row
                if (row == 0 && col == bottles.length - 1) {
                    System.out.println();
                    System.out.print("---#---");
                } else {
                    System.out.print(" ");
                }
                index++;
            }
            System.out.println();
        }
    }
    public boolean select(int bottleNumber) {
        // Check if the bottleNumber is valid
        if (bottleNumber < 1 || bottleNumber > bottles.length) {
            System.out.println("Invalid bottle number!");
            return false;
        }
        selectedBottle = bottles[bottleNumber - 1];
        // Check if the selected bottle is empty
        if (selectedBottle.isEmpty() /*|| selectedBottle.getStackSize() == maxBottleSize*/) {
            System.out.println("Cannot select an empty bottle");
            return false;
        }
        //check if the selected bottle ist full with same color
        if (selectedBottle.getStackSize() == maxBottleSize && selectedBottle.checkStackUniform()) {
            System.out.println("Cannot select a full bottle with same colored");
            return false;
        }
        // Mark the selected bottle as selected
        ColorBottle topBottle = selectedBottle.pop();
        topBottle.setSelected(true);
        selectedBottle.push(topBottle);
        System.out.println("Bottle " + bottleNumber + " has been selected!");
        selectedBottleNumber = bottleNumber;
        //saveSelect
        saveSelect.push(bottleNumber);
        UndoAction undoSelect = new UndoAction("select",selectedBottleNumber);
        undoStack.push(undoSelect);
        return true;
    }
    private void undoSelect(int bottleNumber) {
        int previousSelectedBottle = selectedBottleNumber;
        saveSelect.pop();
        if (!saveSelect.isEmpty()) {
            int newSelectedBottle = (int) saveSelect.peek();
            selectedBottleNumber = newSelectedBottle;
            if (previousSelectedBottle != newSelectedBottle) {
                selectedBottle = bottles[newSelectedBottle - 1];

                if (!selectedBottle.isEmpty()) {
                    ColorBottle newSelectedTop = selectedBottle.pop();
                    newSelectedTop.setSelected(true);
                    selectedBottle.push(newSelectedTop);
                }
                StackAsMyArrayList<ColorBottle> previousSelected = bottles[previousSelectedBottle - 1];
                if (!previousSelected.isEmpty()) {
                    ColorBottle previousSelectedTop = previousSelected.pop();
                    previousSelectedTop.setSelected(false);
                    previousSelected.push(previousSelectedTop);
                }
            }
        }
        RedoAction redoSelect = new RedoAction("select", previousSelectedBottle);
        redoStack.push(redoSelect);

        System.out.println("Undo: Selection has been undone.");
    }
    private void redoSelect(int bottleNumber) {
        int newSelectedBottle = bottleNumber;
        selectedBottleNumber = newSelectedBottle;
        selectedBottle = bottles[newSelectedBottle - 1];
        if (!selectedBottle.isEmpty()) {
            ColorBottle newSelectedTop = selectedBottle.pop();
            newSelectedTop.setSelected(true);
            selectedBottle.push(newSelectedTop);
        }
    }
    public void deSelected() {
        if (selectedBottle == null) {
            System.out.println("No bottle is currently selected.");
            return;
        }
        UndoAction undoDeSelect = new UndoAction("deSelect",selectedBottleNumber);
        undoStack.push(undoDeSelect);
        // Get the top bottle from the selected stack
        ColorBottle topBottle = (ColorBottle) selectedBottle.pop();
        // Mark the bottle as not selected
        topBottle.setSelected(false);
        // Push the updated bottle back to the stack
        selectedBottle.push(topBottle);
        // Print a message indicating successful deselection
        System.out.println("Bottle deselected.");
        selectedBottleNumber = -1;
        // Set selectedBottle to null to indicate that there is no bottle selected
        selectedBottle = null;
    }
    public void undoDeSelect(int bottleNumber){
        selectedBottle = bottles[bottleNumber - 1];
        ColorBottle topBottle = selectedBottle.pop();
        topBottle.setSelected(true);
        selectedBottle.push(topBottle);
        selectedBottleNumber = bottleNumber;
        RedoAction redoDeSelect = new RedoAction("deSelect");
        redoStack.push(redoDeSelect);
    }
    public void redoDeSelect(){
        ColorBottle topBottle = (ColorBottle) selectedBottle.pop();
        // Mark the bottle as not selected
        topBottle.setSelected(false);
        // Push the updated bottle back to the stack
        selectedBottle.push(topBottle);
        selectedBottleNumber = -1;
        selectedBottle = null;
    }
    public void selectNext() {
        if (selectedBottle == null) {
            System.out.println("No bottle is currently selected.");
            return;
        }
        int nextIndex = (selectedBottleNumber) % bottles.length;
        select(nextIndex + 1);
    }
    public void selectPrevious() {
        if (selectedBottle == null) {
            System.out.println("No bottle is currently selected.");
            return;
        }
        int prevIndex = (selectedBottleNumber - 2 + bottles.length) % bottles.length;
        select(prevIndex + 1);
    }
    public boolean pour(int bottleNumber) {
        int sw = 0;
        if (selectedBottle == null) {
            System.out.println("No bottle is currently selected.");
            return false;
        }
        // Check if the selected bottle is empty
        if (selectedBottle.isEmpty()) {
            System.out.println("Cannot pour from an empty bottle.");
            return false;
        }
        // Get the color of the selected bottle
        String selectedColor = ((ColorBottle) selectedBottle.peek()).getColor();
        // Get the target bottle for pouring
        StackAsMyArrayList<ColorBottle> targetBottle = bottles[bottleNumber - 1];
        if(bottleNumber - 1 == bottles.length-1){
            sw = 1;
        }
        if (targetBottle.isEmpty() || targetBottle.peek().getColor().equals(selectedColor)) {
            // Pour the color from the selected bottle to the target bottle
            if (targetBottle.getStackSize() < maxBottleSize && sw==0) {
                targetBottle.push(selectedBottle.pop());
                UndoAction undoPour = new UndoAction("pour",selectedBottleNumber,bottleNumber);
                undoStack.push(undoPour);
                display();
            }
            else if(targetBottle.getStackSize() < maxBottleSize/2 && sw==1){
                targetBottle.push(selectedBottle.pop());
                UndoAction undoPour = new UndoAction("pour",selectedBottleNumber,bottleNumber);
                undoStack.push(undoPour);
                display();
            }
            else {
                System.out.println("\nBottle " + bottleNumber + " is full\n");
                display();
                return false;
            }
        } else {
            System.out.println("Cannot pour this color(not same)");
        }
        return true;
    }
    public void undoPour(int bottleNumber){
        String selectedColor = ((ColorBottle) selectedBottle.peek()).getColor();
        StackAsMyArrayList<ColorBottle> targetBottle = bottles[bottleNumber - 1];
        selectedBottle.push(targetBottle.pop());
        RedoAction redoSelect = new RedoAction("pour",selectedBottleNumber,bottleNumber);
        redoStack.push(redoSelect);
    }
    public void redoPour(int bottleNumber){
        String selectedColor = ((ColorBottle) selectedBottle.peek()).getColor();
        // Get the target bottle for pouring
        StackAsMyArrayList<ColorBottle> targetBottle = bottles[bottleNumber - 1];
        if (targetBottle.isEmpty() || targetBottle.peek().getColor().equals(selectedColor)) {
            // Pour the color from the selected bottle to the target bottle
            if (targetBottle.getStackSize() < maxBottleSize) {
                targetBottle.push(selectedBottle.pop());
            }
        }
    }
    public void swap(int newBottleNumber) {
        if (selectedBottle == null) {
            System.out.println("No bottle is currently selected.");
            return;
        }
        // Pop all bottles from the selected stack
        StackAsMyArrayList<ColorBottle> selectedBottles = new StackAsMyArrayList<>();
        while (!selectedBottle.isEmpty()) {
            selectedBottles.push(selectedBottle.pop());
        }
        // Get the bottle stack for the specified new bottle number
        StackAsMyArrayList<ColorBottle> newBottleStack = bottles[newBottleNumber - 1];
        // Pop all bottles from the new bottle stack
        StackAsMyArrayList<ColorBottle> newBottles = new StackAsMyArrayList<>();
        while (!newBottleStack.isEmpty()) {
            newBottles.push(newBottleStack.pop());
        }
        // Swap the bottle stacks
        while (!selectedBottles.isEmpty()) {
            newBottleStack.push(selectedBottles.pop());
        }
        while (!newBottles.isEmpty()) {
            selectedBottle.push(newBottles.pop());
        }
        UndoAction undoSwap = new UndoAction("swap",selectedBottleNumber,newBottleNumber);
        undoStack.push(undoSwap);
        display();
    }
    private void undoSwap(int newBottleNumber) {
        StackAsMyArrayList<ColorBottle> selectedBottles = new StackAsMyArrayList<>();
        while (!selectedBottle.isEmpty()) {
            selectedBottles.push(selectedBottle.pop());
        }
        // Get the bottle stack for the specified new bottle number
        StackAsMyArrayList<ColorBottle> newBottleStack = bottles[newBottleNumber - 1];
        // Pop all bottles from the new bottle stack
        StackAsMyArrayList<ColorBottle> newBottles = new StackAsMyArrayList<>();
        while (!newBottleStack.isEmpty()) {
            newBottles.push(newBottleStack.pop());
        }
        // Swap the bottle stacks
        while (!selectedBottles.isEmpty()) {
            newBottleStack.push(selectedBottles.pop());
        }
        while (!newBottles.isEmpty()) {
            selectedBottle.push(newBottles.pop());
        }
        RedoAction redoSwap = new RedoAction("swap",selectedBottleNumber,newBottleNumber);
        redoStack.push(redoSwap);
    }
    private void redoSwap(int newBottleNumber){
        StackAsMyArrayList<ColorBottle> selectedBottles = new StackAsMyArrayList<>();
        while (!selectedBottle.isEmpty()) {
            selectedBottles.push(selectedBottle.pop());
        }
        // Get the bottle stack for the specified new bottle number
        StackAsMyArrayList<ColorBottle> newBottleStack = bottles[newBottleNumber - 1];
        // Pop all bottles from the new bottle stack
        StackAsMyArrayList<ColorBottle> newBottles = new StackAsMyArrayList<>();
        while (!newBottleStack.isEmpty()) {
            newBottles.push(newBottleStack.pop());
        }
        // Swap the bottle stacks
        while (!selectedBottles.isEmpty()) {
            newBottleStack.push(selectedBottles.pop());
        }
        while (!newBottles.isEmpty()) {
            selectedBottle.push(newBottles.pop());
        }
    }
    public void replaceColor(String firstColor, String secondColor) {
        boolean foundOldColor = false;
        boolean foundNewColor = false;
        // Iterate over all bottles to find and replace the colors
        for (int i = 0; i < bottles.length; i++) {
            StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
            if (bottleStack != null) {
                for (int j = 0; j < bottleStack.getStackSize(); j++) {
                    ColorBottle bottle = bottleStack.get(j);
                    if (bottle.getColor().equals(firstColor)) {
                        foundOldColor = true;
                    }
                    if (bottle.getColor().equals(secondColor)) {
                        foundNewColor = true;
                    }
                }
            }
        }
        // Check if both firstColor and secondColor were found
        if (foundOldColor && !foundNewColor) {
            // Iterate over all bottles again to perform the replacement
            for (int i = 0; i < bottles.length; i++) {
                StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
                if (bottleStack != null) {
                    for (int j = 0; j < bottleStack.getStackSize(); j++) {
                        ColorBottle bottle = bottleStack.get(j);
                        if (bottle.getColor().equals(firstColor)) {
                            bottle.setColor(secondColor);
                        }
                    }
                }
            }
            System.out.println("Color replaced successfully.");
            UndoAction undoRep = new UndoAction("replaceColor",secondColor,firstColor);
            undoStack.push(undoRep);
            display();
        } else {
            System.out.println("Cannot replace color. Either first color not found or second color already exists.");
        }
    }
    public void undoReplaceColor(String firstColor, String secondColor) {
        boolean foundOldColor = false;
        boolean foundNewColor = false;
        // Iterate over all bottles to find and replace the colors
        for (int i = 0; i < bottles.length; i++) {
            StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
            if (bottleStack != null) {
                for (int j = 0; j < bottleStack.getStackSize(); j++) {
                    ColorBottle bottle = bottleStack.get(j);
                    if (bottle.getColor().equals(firstColor)) {
                        foundOldColor = true;
                    }
                    if (bottle.getColor().equals(secondColor)) {
                        foundNewColor = true;
                    }
                }
            }
        }
        // Check if both firstColor and secondColor were found
        if (foundOldColor && !foundNewColor) {
            // Iterate over all bottles again to perform the replacement
            for (int i = 0; i < bottles.length; i++) {
                StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
                if (bottleStack != null) {
                    for (int j = 0; j < bottleStack.getStackSize(); j++) {
                        ColorBottle bottle = bottleStack.get(j);
                        if (bottle.getColor().equals(firstColor)) {
                            bottle.setColor(secondColor);
                        }
                    }
                }
            }
        }
        RedoAction redoReplaceColor = new RedoAction("replaceColor",secondColor,firstColor);
        redoStack.push(redoReplaceColor);
    }
    private void redoRep(String firstColor, String secondColor){
        //replaceColor(firstColor,secondColor);
        boolean foundOldColor = false;
        boolean foundNewColor = false;
        // Travers all bottles to find and replace the colors khkhk
        for (int i = 0; i < bottles.length; i++) {
            StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
            if (bottleStack != null) {
                for (int j = 0; j < bottleStack.getStackSize(); j++) {
                    ColorBottle bottle = bottleStack.get(j);
                    if (bottle.getColor().equals(firstColor)) {
                        foundOldColor = true;
                    }
                    if (bottle.getColor().equals(secondColor)) {
                        foundNewColor = true;
                    }
                }
            }
        }
        // Check if both firstColor and secondColor were found
        if (foundOldColor && !foundNewColor) {
            //Traverse all bottles again to perform the replacement
            for (int i = 0; i < bottles.length; i++) {
                StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];
                if (bottleStack != null) {
                    for (int j = 0; j < bottleStack.getStackSize(); j++) {
                        ColorBottle bottle = bottleStack.get(j);
                        if (bottle.getColor().equals(firstColor)) {
                            bottle.setColor(secondColor);
                        }
                    }
                }
            }
        }
    }
    public void addEmptyBottle() {
        if (emptyBottleAdded) {
            System.out.println("You can only add an empty bottle once.");
            return;
        }
        // Create an empty bottle stack with size equal to maxBottleSize
        emptyBottleStack = new StackAsMyArrayList<>();
        for (int i = 0; i < maxBottleSize / 2; i++) {
            ColorBottle emptyBottle = new ColorBottle("Empty");
            emptyBottleStack.push(emptyBottle);
            emptyBottleStack.clear();
        }
        // Add the empty bottle stack to the end of bottles
        StackAsMyArrayList<ColorBottle>[] newBottles = new StackAsMyArrayList[bottles.length + 1];
        System.arraycopy(bottles, 0, newBottles, 0, bottles.length);
        newBottles[bottles.length] = emptyBottleStack;
        bottles = newBottles;
        int newBot = bottles.length;

        UndoAction undoAddEmptyBottle = new UndoAction("addEmptyBottle", newBot, emptyBottleStack);
        undoStack.push(undoAddEmptyBottle);
        emptyBottleAdded = true;
        System.out.println("An empty bottle has been added.");
        display();
    }
    public void undoAddEmptyBottle(int bottleNumber, StackAsMyArrayList<ColorBottle> removedBottleStack) {
        // Remove the last added empty bottle stack from bottles
        StackAsMyArrayList<ColorBottle>[] newBottles = new StackAsMyArrayList[bottles.length - 1];
        System.arraycopy(bottles, 0, newBottles, 0, bottles.length - 1);
        bottles = newBottles;

        RedoAction redoAddEmptyBottle = new RedoAction("addEmptyBottle", removedBottleStack);
        redoStack.push(redoAddEmptyBottle);

        emptyBottleAdded = false;
        System.out.println("Undo: An empty bottle has been removed.");
    }
    public void redoAddEmptyBottle(StackAsMyArrayList<ColorBottle> addedBottleStack) {
        // Add the previously removed empty bottle stack back to bottles
        StackAsMyArrayList<ColorBottle>[] newBottles = new StackAsMyArrayList[bottles.length + 1];
        System.arraycopy(bottles, 0, newBottles, 0, bottles.length);
        newBottles[bottles.length] = addedBottleStack;
        bottles = newBottles;

//        UndoAction undoAddEmptyBottle = new UndoAction("addEmptyBottle", bottles.length, addedBottleStack);
//        undoStack.push(undoAddEmptyBottle);

        emptyBottleAdded = true;
        System.out.println("Redo: An empty bottle has been added.");
    }
    public boolean hasWon() {
        return SameColor() /*&& hasExactlyOneEmptyBottle()*/;
    }
    public boolean SameColor(){
        int count = 0;
        for(int i = 0 ; i < bottles.length; i++){
            if(bottles[i].checkStackUniform() && bottles[i].getStackSize() == maxBottleSize){
                count++;
            }
        }
        if(count == bottles.length-1 && !emptyBottleAdded)
            return true;
        else if(count == bottles.length-2 && emptyBottleAdded){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean hasExactlyOneEmptyBottle() {
        int emptyBottleCount = 0;
        for (int i = 0; i < bottles.length; i++) {
            StackAsMyArrayList<ColorBottle> bottleStack = bottles[i];

                // Check if the bottle is completely empty
                if (bottles[i].isEmpty()) {
                    emptyBottleCount++;

                    // If more than one empty bottle found, return false
                    if (emptyBottleCount > 1) {
                        return false;
                    }
            }
        }
        // Check if there is exactly one empty bottle
        return emptyBottleCount == 1;
    }
    public void undo() {
        if (!undoStack.isEmpty() && undoCount < 5) {
            UndoAction undoAction = undoStack.pop();

            switch (undoAction.getActionType()) {
                case "select":
                case "selectNext":
                case "selectPrevious":
                    undoSelect(undoAction.getBottleNumber());
                    undoCount++;
                    break;
                case "deSelect":
                    undoDeSelect(undoAction.getBottleNumber());
                    undoCount++;
                    break;
                case "pour":
                    undoPour(undoAction.getBottleNumber());
                    undoCount++;
                    break;
                case "swap":
                    undoSwap(undoAction.getBottleNumber());
                    undoCount++;
                    break;
                case "replaceColor":
                    undoReplaceColor(undoAction.getFirstColor(),undoAction.getSecondColor());
                    undoCount++;
                    break;
                case "addEmptyBottle":
                    undoAddEmptyBottle(undoAction.getBottleNumber(),emptyBottleStack);
                    undoCount++;
                    break;
            }
            display();
        } else {
            System.out.println("Cannot undo further or no actions to undo.");
        }
    }
    public void redo(){
        if(!redoStack.isEmpty() && redoCount < 5){
            RedoAction redoAction = redoStack.pop();
            switch (redoAction.getActionType()){
                case "select":
                case "selectNext":
                case "selectPrevious":
                    redoSelect(redoAction.getBottleNumber());
                    redoCount++;
                    break;
                case "deSelect":
                    redoDeSelect();
                    redoCount++;
                    break;
                case "pour":
                    redoPour(redoAction.getBottleNumber());
                    redoCount++;
                    break;
                case "swap":
                    redoSwap(redoAction.getBottleNumber());
                    redoCount++;
                    break;
                case "replaceColor":
                    redoRep(redoAction.getFirstColor(),redoAction.getSecondColor());
                    redoCount++;
                    break;
                case "addEmptyBottle":
                    redoAddEmptyBottle(redoAction.getRemoveBottle());
                    undoCount++;
                    break;
            }
            display();
        }
        else{
            System.out.println("Redo stack is empty. Cannot redo.");
        }
    }
}

class ColorBottle implements Comparable<ColorBottle>{
    private StackAsMyArrayList<ColorBottle> bottleStack;
    private String color;
    private int number = 0;
    private boolean isSelected;
    private boolean isEmpty;

    public ColorBottle(String color) {
        this.color = color;
        isSelected = false;
        this.isEmpty = false;
    }
    public ColorBottle() {
        this.color = color;
        isSelected = false;
        this.isEmpty = false;
    }
    public int getNumber() {
        return number;
    }
    public void setColor(String color){
        if(isEmpty){
            bottleStack.push(this);
        }
        this.color = color;
    }
    public void setBottleStack(StackAsMyArrayList<ColorBottle> bottleStack) {
        this.bottleStack = bottleStack;
    }

    public StackAsMyArrayList<ColorBottle> getBottleStack() {
        return bottleStack;
    }
    public String getColor() {
        return color;
    }
    public ColorBottle select() {
        this.isSelected = true;
        return this;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isEmpty() {
        return isEmpty;
    }

    public void makeEmpty() {
        isEmpty = true;
    }

    @Override
    public int compareTo(ColorBottle other) {
        return Integer.compare(this.number, other.number);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ColorBottle other = (ColorBottle) obj;
        return Objects.equals(color, other.color);
    }


}