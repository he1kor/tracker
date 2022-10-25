package com.helkor.project.buttons.Utils;

import java.util.List;

public class Deck <T>{
    List<T> elements;
    int size;
    int pointer;

    public Deck(List<T> elements, int pointer){
        this.elements = elements;
        size = elements.size();
        this.pointer = pointer;
    }
    public Deck(List<T> elements) {
        this.elements = elements;
        size = elements.size();
        pointer = 0;
    }
    public T face(){
        return elements.get(pointer);
    }
    public T next(){
        movePointer();
        return elements.get(pointer);
    }
    private void movePointer(){
        if (pointer == size-1){
            pointer = 0;
            return;
        }
        pointer++;
    }
}
