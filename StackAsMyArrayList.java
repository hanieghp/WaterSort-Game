
public class StackAsMyArrayList<E>
{
    MyArrayList<E> theStack;
    public StackAsMyArrayList() {
        theStack = new MyArrayList<E>();
    }
    public void push(E newElement) //insert at end of array!
    {
        if (!theStack.checkSpace())
            throw new IndexOutOfBoundsException("Stack out of bounds");

        theStack.add(theStack.getSize(), newElement);
        // Check if all elements of the bottle are Empty, then mark the bottle as empty
        if (newElement instanceof ColorBottle) {
            ColorBottle bottle = (ColorBottle) newElement;
            if (bottle.isEmpty()) {
                bottle.makeEmpty();
            }
        }
    }
    public E pop() //remove end of array
    {
        E temp = null;
        boolean isDone = false;
        if (theStack.getSize() > 0)
            temp=theStack.remove(theStack.getSize()-1);
        return temp; // temp will be null in special case of empty list
    }
    public E peek() {
        E temp = null;

        if(theStack.getSize() > 0) {
            temp = theStack.get(theStack.getSize()-1);
        }
        return temp;
    }
    public int getStackSize() {
        return theStack.getSize();
    }
    public void setSize(int size){
        size = getStackSize();
    }
    public E get(int index) {
        return theStack.get(index);
    }
    public boolean checkStackUniform() {
        return this.theStack.checkUniform();
    }
    public void clear()
    {
        theStack.clear();
    }

    public String toString()
    {
        return theStack.toString();
    }
    public boolean isEmpty() {
        return theStack.getSize() == 0;
    }
}
