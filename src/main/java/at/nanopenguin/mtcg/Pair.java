package at.nanopenguin.mtcg;

import lombok.Setter;

public class Pair<T, U> {
    @Setter
    private T left;
    @Setter
    private U right;

    public Pair (T left, U right) {
        this.left = left;
        this.right = right;
    }

    public T left() { return this.left; };
    public U right() { return this.right; };
}
