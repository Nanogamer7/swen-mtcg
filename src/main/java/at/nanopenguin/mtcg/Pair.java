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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair<?,?>) {
            return left.equals(((Pair<?, ?>) o).left) && right.equals(((Pair<?, ?>) o).right);
        }

        return super.equals(o);
    }
}
