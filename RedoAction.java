class RedoAction {
    private String actionType;
    private int bottleNumber,selectedNum;
    private String firstColor,secondColor;
    private StackAsMyArrayList<ColorBottle> removeBottle;

    public int getSelectedNum() {
        return selectedNum;
    }

    public String getFirstColor() {
        return firstColor;
    }

    public String getSecondColor() {
        return secondColor;
    }
    public RedoAction(String actionType){
        this.actionType = actionType;
    }
    public RedoAction(String actionType, int bottleNumber) {
        this.actionType = actionType;
        this.bottleNumber = bottleNumber;
    }
    public RedoAction(String actionType,int selectedNum,int bottleNumber){
        this.actionType = actionType;
        this.bottleNumber = bottleNumber;
        this.selectedNum = selectedNum;
    }
    public RedoAction(String actionType ,String firstColor, String secondColor){
        this.actionType = actionType;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }
    public RedoAction(String actionType, StackAsMyArrayList<ColorBottle> removeBottle){
        this.actionType = actionType;
        this.removeBottle = removeBottle;
    }

    public StackAsMyArrayList<ColorBottle> getRemoveBottle() {
        return removeBottle;
    }

    public String getActionType() {
        return actionType;
    }

    public int getBottleNumber() {
        return bottleNumber;
    }
}
