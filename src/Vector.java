class Vector {

    float x;
    float y;

    Vector(float x, float y) {
        set(x, y);
    }

    Vector(Vector v) {
        this(v.x, v.y);
    }

    void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void set(Vector v) {
        set(v.x, v.y);
    }

    void scale(float s) {
        this.x *= s;
        this.y *= s;
    }

    Vector createScale(float s) {
        Vector newVec = new Vector(this.x * s, this.y * s);
        return newVec;
    }

    void scale(Vector v) {
        this.x *= v.x;
        this.y *= v.y;
    }

    Vector createScale(Vector v) {
        Vector newVec = new Vector(this.x * v.x, this.y * v.y);
        return newVec;
    }

    void add(Vector v) {
        this.x += v.x;
        this.y += v.y;
    }

    Vector createAdd(Vector v) {
        Vector newVec = new Vector(this.x + v.x, this.y + v.y);
        return newVec;
    }

}
