class UndoAction {
    private String actionType;
    private int bottleNumber,selectedNum;
    private StackAsMyArrayList<ColorBottle> addedBottles;

    public String getFirstColor() {
        return firstColor;
    }

    public String getSecondColor() {
        return secondColor;
    }

    private String firstColor,secondColor;

    public int getSelectedNum() {
        return selectedNum;
    }

    public UndoAction(String actionType, int bottleNumber) {
        this.actionType = actionType;
        this.bottleNumber = bottleNumber;
    }
    public UndoAction(String actionType,int selectedNum,int bottleNumber){
        this.actionType = actionType;
        this.bottleNumber = bottleNumber;
        this.selectedNum = selectedNum;
    }
    public UndoAction(String actionType ,String firstColor, String secondColor){
        this.actionType = actionType;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }
    public UndoAction(String actionType, int bottleNumber, StackAsMyArrayList<ColorBottle> addedBottles) {
        this.actionType = actionType;
        this.bottleNumber = bottleNumber;
        this.addedBottles = addedBottles;
    }

    public String getActionType() {
        return actionType;
    }

    public int getBottleNumber() {
        return bottleNumber;
    }
    public StackAsMyArrayList<ColorBottle> getAddedBottles() {
        return addedBottles;
    }
}
