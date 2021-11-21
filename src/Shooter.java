import java.util.ArrayList;
import processing.core.PApplet;

// ゲーム本体
public class Shooter extends PApplet {
    static final Vector SCREEN_SIZE = new Vector(500, 750); // 画面サイズ
    static final Vector PLAYER_VELOCITY = new Vector(200, 200); // 自機の速度
    static final Vector BULLET_VELOCITY = new Vector(0, -500); // 弾の速度
    static final Vector ENEMY_BULLET_VELOCITY = new Vector(0, 300); // 弾の速度
    static final float TIME_STEP = 1 / 60.0f; // 1フレームの時間

    static final int MENU = 0; // メニュー
    static final int PLAY = 1; // ゲームプレイ
    static final int RETIRE = 2; // リタイア
    static final int CLEAR = 3; // クリア
    static final int GAME_OVER = 4; // ゲームオーバ

    abstract class ShooterObject {
        Vector position; // 位置
        boolean removeFlag; // 消去するかどうか

        // コンストラクタ
        ShooterObject(Vector position) {
            this.position = new Vector(position);
            removeFlag = false;
        }

        // 自機かどうか
        boolean isPlayer() {
            return false;
        }

        // 弾かどうか
        boolean isBullet() {
            return false;
        }

        // 敵かどうか
        boolean isEnemy() {
            return false;
        }

        // 敵の弾かどうか
        boolean isEnemyBullet() {
            return false;
        }

        boolean canRemove() {
            return false;
        }

        int howHitPoint() {
            return 0;
        }

        void setHitPoint() {
        }

        // 時間 dt 分の移動
        abstract void step(float dt);

        // 敵 enemy との衝突判定
        boolean checkCollision(Enemy enemy) {
            return false;
        }

        // 敵弾 との衝突判定
        boolean checkCollisionEnemyBullet(EnemyBullet enemyBullet) {
            return false;
        }

        // Processing 画面への描画
        abstract void draw();
    }

    // 自機
    class Player extends ShooterObject {

        Vector direction; // 移動方向
        int HitPoint;

        // コンストラクタ
        Player(Vector position, int HitPoint) {
            super(position);
            direction = new Vector(0, 0);
            this.HitPoint = HitPoint;
        }

        boolean isPlayer() {
            return true;
        }

        // 時間dt分の移動
        void step(float dt) {
        }

        int howHitPoint() {
            return HitPoint;
        }

        void setHitPoint() {
            HitPoint--;
        }

        // 敵enemyとの衝突判定
        boolean checkCollision(Enemy enemy) {
            return position.x > enemy.position.x - 10 && position.x < enemy.position.x + 10
                    && position.y > enemy.position.y - 10 && position.y < enemy.position.y + 10;
        }

        // 敵弾との衝突判定
        boolean checkCollisionEnemyBullet(EnemyBullet enemyBullet) {
            return position.x > enemyBullet.position.x - 10 && position.x < enemyBullet.position.x + 10
                    && position.y > enemyBullet.position.y - 10 && position.y < enemyBullet.position.y + 10;
        }

        // Processing画面への描画
        void draw() {
            fill(64, 192, 255);
            beginShape();
            vertex(position.x, position.y - 10);
            vertex(position.x + 10, position.y + 10);
            vertex(position.x - 10, position.y + 10);
            endShape(CLOSE);
        }

    }

    // 弾
    class Bullet extends ShooterObject {

        Vector velocity; // 速度

        // コンストラクタ
        Bullet(Vector position, Vector velocity) {
            super(position);
            this.velocity = new Vector(velocity);
        }

        boolean isBullet() {
            return true;
        }

        // 時間dt分の移動
        void step(float dt) {
            position.add(velocity.createScale(dt));
            if (position.y < -10) {
                removeFlag = true;
            }
            if (position.y > Shooter.SCREEN_SIZE.y + 10) {
                removeFlag = true;
            }
            if (position.x > Shooter.SCREEN_SIZE.x + 10) {
                removeFlag = true;
            }
            if (position.x < -10) {
                removeFlag = true;
            }
        }

        // 敵enemyとの衝突判定
        boolean checkCollision(Enemy enemy) {
            return position.x > enemy.position.x - 10 && position.x < enemy.position.x + 10
                    && position.y > enemy.position.y - 10 && position.y < enemy.position.y + 10;
        }

        // 敵弾との衝突判定
        boolean checkCollisionEnemyBullet(EnemyBullet enemyBullet) {
            return position.x > enemyBullet.position.x - 10 && position.x < enemyBullet.position.x + 10
                    && position.y > enemyBullet.position.y - 10 && position.y < enemyBullet.position.y + 10;
        }

        // Processing画面への描画
        void draw() {
            fill(255, 255, 128);
            // rect(position.x - 2, position.y - 10, 4, 20);
            ellipse(position.x - 2, position.y - 10, 10, 10);
        }

    }

    // 敵
    class Enemy extends ShooterObject {

        Vector velocity; // 速度
        float center; // 左右の揺れの中心のx座標
        float factor; // 左右の揺れの係数
        int HitPoint; // HP

        float in_time; // 画面に出てくる時間
        float stop_time; // 画面から消える時間
        float out_time;
        int enemy_pattern;
        float all_time;

        // コンストラクタ
        Enemy(Vector position, int HitPoint, float in_time, float stop_time, float out_time, int enemy_pattern) {
            super(position);
            this.HitPoint = HitPoint;
            this.in_time = in_time;
            this.stop_time = stop_time;
            this.out_time = out_time;
            this.enemy_pattern = enemy_pattern;
            this.all_time = 0;
        }

        boolean isEnemy() {
            return true;
        }

        int howHitPoint() {
            return HitPoint;
        }

        void setHitPoint() {
            HitPoint--;
        }

        // 時間dt分の移動
        void step(float dt) {
            if (!removeFlag) {
                all_time += dt;
                switch (enemy_pattern) {
                case 0:
                    if (in_time < all_time && all_time < stop_time) {
                        position.y += 2;
                    } else if (all_time > out_time) {
                        position.y -= 2;
                    }
                    break;

                case 1:
                    if (in_time < all_time && all_time < out_time) {
                        position.x += 1.5f;
                        position.y += 0.9f;
                    }
                    break;
                }
                if (position.y < -100) {
                    removeFlag = true;
                }
                if (position.y > Shooter.SCREEN_SIZE.y + 10) {
                    removeFlag = true;
                }
            }
        }

        // Processing 画面への描画
        void draw() {
            fill(255, 64, 128);
            // ellipse(position.x, position.y, 20, 20);
            beginShape();
            vertex(position.x + 10, position.y - 10);
            vertex(position.x - 10, position.y - 10);
            vertex(position.x, position.y + 10);
            endShape(CLOSE);
        }

    }

    // 敵弾
    class EnemyBullet extends ShooterObject {

        Vector velocity; // 速度
        int id; // 弾の出す種類
        boolean Remove;

        // コンストラクタ
        EnemyBullet(Vector position, Vector velocity, int id, boolean Remove) {
            super(position);
            this.velocity = new Vector(velocity);
            this.id = id;
            this.Remove = Remove;
        }

        boolean isEnemyBullet() {
            return true;
        }

        boolean canRemove() {
            return Remove;
        }

        // 時間dt分の移動
        void step(float dt) {
            position.add(velocity.createScale(dt));
            if (position.y < -10) {
                removeFlag = true;
            }
            if (position.y > Shooter.SCREEN_SIZE.y + 10) {
                removeFlag = true;
            }
            if (position.x > Shooter.SCREEN_SIZE.x + 10) {
                removeFlag = true;
            }
            if (position.x < -10) {
                removeFlag = true;
            }
        }

        // Processing画面への描画
        void draw() {
            // fill(255, 255, 128);
            if (id == 0) {
                fill(255, 0, 0);
                ellipse(position.x + 2, position.y + 10, 10, 10);
            } else if (id == 1) {
                fill(0, 255, 0);
                // rect(position.x - 2, position.y - 10, 4, 20);
                ellipse(position.x + 2, position.y + 10, 10, 10);
            } else if (id == 2) {
                fill(255, 255, 255);
                // rect(position.x - 2, position.y - 10, 4, 20);
                ellipse(position.x + 2, position.y + 10, 10, 10);
            }

        }

    }

    boolean bulletShootFlag; // 弾を撃つか
    boolean EnemyBulletShootFlag; // 敵弾を撃つか

    int GAME_MODE = 0; // ゲームモード

    ArrayList<ShooterObject> playerList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> EnemyList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> playerBulletList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> EnemyBulletList = new ArrayList<ShooterObject>();
    int BulletCount = 0;
    int EnemyBulletCount = 0;

    // 初期化
    void initialize() {
        playerList.clear();
        playerList.add(new Player(SCREEN_SIZE.createScale(new Vector(0.5f, 0.9f)), 10));

        EnemyList.clear();
        playerBulletList.clear();
        EnemyBulletList.clear(); // 敵の弾クリア
        EnemyBulletShootFlag = false;

        bulletShootFlag = false;

        BulletCount = 0;
        EnemyBulletCount = 0;

        // 敵の位置・速度の初期値と揺れの係数をランダムに決定

        for (int i = 0; i < 1; i++) {
            Vector enemyPosition;
            enemyPosition = SCREEN_SIZE.createScale(new Vector(0.5f, -0.1f));
            EnemyList.add(new Enemy(enemyPosition, 5, 0, 2, 100, 0));
            // Enemy(Vector position, int HitPoint, float in_time, float stop_time, float
            // out_time, int enemy_pattern)
            // for (int t = 0; t < 6; t++) {
            // enemyPosition = SCREEN_SIZE.createScale(new Vector(-0.1f, -0.1f));
            // EnemyList.add(new Enemy(enemyPosition, 4, 0 + (0.5f * t), 2, 100, 1));
            // }

        }

    }

    // Processingのsettingsメソッド
    public void settings() {
        size((int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
    }

    // Processingのsetupメソッド
    public void setup() {
        initialize();
    }

    void verticalShot(int x) {
        for (int i = 0; i < EnemyList.size(); i++) {
            EnemyBulletList.add(new EnemyBullet(EnemyList.get(i).position, new Vector(0, x), 0, false));
        }
    }

    void snipeShot(int x) { // 自機に対して向かってくる弾
        for (int i = 0; i < EnemyList.size(); i++) {
            float xlength = playerList.get(0).position.x - EnemyList.get(i).position.x;
            float ylength = playerList.get(0).position.y - EnemyList.get(i).position.y;
            EnemyBulletList
                    .add(new EnemyBullet(EnemyList.get(i).position,
                            new Vector(x * xlength / ((float) Math.sqrt(xlength * xlength + ylength * ylength)),
                                    x * ylength / ((float) Math.sqrt(xlength * xlength + ylength * ylength))),
                            0, true));
        }
    }

    void circleShot(int x) { // 敵の円状の弾
        int DIV = 360;
        for (int t = 0; t < EnemyList.size(); t++) {
            for (int i = 0; i < DIV; i += 5) {
                double rad = Math.toRadians(i);
                EnemyBulletList.add(new EnemyBullet(EnemyList.get(t).position,
                        new Vector(x * (float) Math.cos(rad), x * (float) Math.sin(rad)), 1, false));
            }
        }
    }

    void swirlShot(int x, int DIV) { // 敵の円状の弾
        double rad = Math.toRadians(DIV);

        for (int i = 0; i < EnemyList.size(); i++) {
            EnemyBulletList.add(new EnemyBullet(EnemyList.get(i).position,
                    new Vector(x * (float) Math.cos(360 - rad), -1 * x * (float) Math.sin(360 - rad)), 2, false));
            EnemyBulletList.add(new EnemyBullet(EnemyList.get(i).position,
                    new Vector(x * (float) Math.cos(rad), x * (float) Math.sin(rad)), 2, false));
            EnemyBulletList.add(new EnemyBullet(EnemyList.get(i).position,
                    new Vector(-1 * x * (float) Math.cos(rad), -1 * x * (float) Math.sin(rad)), 2, false));
            EnemyBulletList.add(new EnemyBullet(EnemyList.get(i).position,
                    new Vector(-1 * x * (float) Math.cos(360 - rad), x * (float) Math.sin(360 - rad)), 2, false));
        }

    }

    // 移動
    void move() {
        if (GAME_MODE == PLAY) {
            if (playerList != null) {
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(0).isPlayer()) {
                        ShooterObject player = playerList.get(0);
                        player.position.set(mouseX, mouseY);
                    }

                }
            }

            if (playerBulletList != null) {
                // 弾を移動
                for (int i = 0; i < playerBulletList.size(); i++) {
                    playerBulletList.get(i).step(TIME_STEP);

                }
            }

            if (EnemyList != null) {
                // 敵を移動
                for (int i = 0; i < EnemyList.size(); i++) {
                    EnemyList.get(i).step(TIME_STEP);

                }
            }

            if (EnemyBulletList != null) {
                // 敵弾を移動
                for (int i = 0; i < EnemyBulletList.size(); i++) {
                    EnemyBulletList.get(i).step(TIME_STEP);

                }
            }

            if (EnemyBulletList != null) {
                for (int i = 0; i < EnemyBulletList.size(); i++) {
                    EnemyBulletList.get(i).step(TIME_STEP);
                }

            }

            // if (EnemyBulletCount % 30 == 0 && EnemyBulletCount != 0) {
            // // verticalShot(90);
            // snipeShot(60);
            // }

            if (EnemyList.get(0).howHitPoint() > 3) {
                if (EnemyBulletCount % 15 == 0 && EnemyBulletCount != 0) {
                    snipeShot(90);
                }
            }

            if (EnemyBulletCount % 150 == 0 && EnemyBulletCount != 0) {
                circleShot(30);
                circleShot(60);
            }

            if (EnemyList.get(0).howHitPoint() <= 2) {
                if (EnemyBulletCount % 4 == 0) {
                    swirlShot(100, EnemyBulletCount % 360);
                }
            }

            if (EnemyBulletCount == 720) {
                EnemyBulletCount = 0;
            } else {
                EnemyBulletCount++;
            }

            if (mousePressed == true) {
                if (BulletCount % 12 == 0) {
                    playerBulletList.add(new Bullet(playerList.get(0).position, BULLET_VELOCITY));
                }

                if (BulletCount == 720) {
                    BulletCount = 0;
                } else {
                    BulletCount++;
                }
            } else {
                BulletCount = 0;
            }

        } else if (GAME_MODE == MENU) {
            if (playerList != null) {
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(0).isPlayer()) {
                        ShooterObject player = playerList.get(0);
                        player.position.set(mouseX, mouseY);
                    }

                }
            }
        }

    }

    // 衝突判定
    void react() {
        if (GAME_MODE == PLAY) {
            if (playerList != null) {
                for (int i = 0; i < playerList.size(); i++) {
                    for (int t = 0; t < EnemyList.size(); t++) {
                        if (playerList.get(i) != null && EnemyList.get(t) != null) {
                            if (EnemyList.get(t).isEnemy()) {
                                if (playerList.get(i).checkCollision((Enemy) EnemyList.get(t))) {
                                    playerList.get(i).setHitPoint();
                                    if (playerList.get(i).howHitPoint() <= 0) {
                                        playerList.get(i).removeFlag = true;
                                    }

                                    EnemyList.get(t).setHitPoint();
                                    if (EnemyList.get(t).howHitPoint() <= 0) {
                                        EnemyList.get(t).removeFlag = true;
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < playerBulletList.size(); i++) {
                    for (int t = 0; t < EnemyList.size(); t++) {
                        if (playerBulletList.get(i) != null && EnemyList.get(t) != null) {
                            if (EnemyList.get(t).isEnemy()) {
                                if (playerBulletList.get(i).checkCollision((Enemy) EnemyList.get(t))) {
                                    playerBulletList.get(i).removeFlag = true;
                                    EnemyList.get(t).setHitPoint();
                                    if (EnemyList.get(t).howHitPoint() <= 0) {
                                        EnemyList.get(t).removeFlag = true;
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < playerList.size(); i++) {
                    for (int t = 0; t < EnemyBulletList.size(); t++) {
                        if (playerList.get(i) != null && EnemyBulletList.get(t) != null) {
                            if (EnemyBulletList.get(t).isEnemyBullet()) {
                                if (playerList.get(i).checkCollisionEnemyBullet((EnemyBullet) EnemyBulletList.get(t))) {
                                    playerList.get(i).setHitPoint();
                                    if (playerList.get(i).howHitPoint() <= 0) {
                                        playerList.get(i).removeFlag = true;
                                    }
                                    EnemyBulletList.get(t).removeFlag = true;
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < playerBulletList.size(); i++) {
                    for (int t = 0; t < EnemyBulletList.size(); t++) {
                        if (playerBulletList.get(i) != null && EnemyBulletList.get(t) != null) {
                            if (EnemyBulletList.get(t).isEnemyBullet() && EnemyBulletList.get(t).canRemove()) {
                                if (playerBulletList.get(i)
                                        .checkCollisionEnemyBullet((EnemyBullet) EnemyBulletList.get(t))) {
                                    playerBulletList.get(i).removeFlag = true;
                                    EnemyBulletList.get(t).removeFlag = true;
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(i) != null && playerList.get(i).removeFlag) {
                        playerList.remove(i);
                    }
                }

                for (int i = 0; i < EnemyList.size(); i++) {
                    if (EnemyList.get(i) != null && EnemyList.get(i).removeFlag) {
                        EnemyList.remove(i);
                    }
                }

                for (int i = 0; i < playerBulletList.size(); i++) {
                    if (playerBulletList.get(i) != null && playerBulletList.get(i).removeFlag) {
                        playerBulletList.remove(i);
                    }
                }

                for (int i = 0; i < EnemyBulletList.size(); i++) {
                    if (EnemyBulletList.get(i) != null && EnemyBulletList.get(i).removeFlag) {
                        EnemyBulletList.remove(i);
                    }
                }
            }
        }
    }

    // 描画
    void doDraw() {
        background(0);
        noStroke();

        int players = 0;
        int enemies = 0;

        if (GAME_MODE == MENU) {
            if (playerList != null) {
                players = playerList.size();
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(i) != null) {
                        playerList.get(i).draw();
                    }
                }
            }

            initialize();
            fill(255, 255, 255);
            textSize(64);
            textAlign(CENTER, CENTER);
            text("Shooting Game", 0.5f * SCREEN_SIZE.x, 0.5f * SCREEN_SIZE.y);
            textSize(32);
            text("-space-", 0.5f * SCREEN_SIZE.x, 0.7f * SCREEN_SIZE.y);
        }

        else if (GAME_MODE == RETIRE) {
            // ゲームオーバー
            if (players == 0) {
                fill(255, 192, 192);
                textSize(64);
                textAlign(CENTER, CENTER);
                text("RETIRE", 0.5f * SCREEN_SIZE.x, 0.5f * SCREEN_SIZE.y);
                textSize(32);
                text("-space-", 0.5f * SCREEN_SIZE.x, 0.7f * SCREEN_SIZE.y);
            }
        }

        else if (GAME_MODE == GAME_OVER) {
            // ゲームオーバー
            fill(255, 192, 192);
            textSize(64);
            textAlign(CENTER, CENTER);
            text("GAME OVER", 0.5f * SCREEN_SIZE.x, 0.5f * SCREEN_SIZE.y);
            textSize(32);
            text("-space-", 0.5f * SCREEN_SIZE.x, 0.7f * SCREEN_SIZE.y);
        }

        else if (GAME_MODE == CLEAR) {
            // ゲームクリア
            fill(192, 255, 192);
            textSize(64);
            textAlign(CENTER, CENTER);
            text("GAME CLEAR", 0.5f * SCREEN_SIZE.x, 0.5f * SCREEN_SIZE.y);
            textSize(32);
            text("-space-", 0.5f * SCREEN_SIZE.x, 0.7f * SCREEN_SIZE.y);
        }

        else if (GAME_MODE == PLAY) {
            if (playerList != null) {
                players = playerList.size();
                for (int i = 0; i < playerList.size(); i++) {
                    if (playerList.get(i) != null) {
                        playerList.get(i).draw();
                    }
                }
            }

            if (EnemyList != null) {
                enemies = EnemyList.size();
                for (int i = 0; i < EnemyList.size(); i++) {
                    if (EnemyList.get(i) != null) {
                        EnemyList.get(i).draw();
                    }
                }
            }

            if (playerBulletList != null) {
                for (int i = 0; i < playerBulletList.size(); i++) {
                    if (playerBulletList.get(i) != null) {
                        playerBulletList.get(i).draw();
                    }
                }
            }

            if (EnemyBulletList != null) {
                for (int i = 0; i < EnemyBulletList.size(); i++) {
                    if (EnemyBulletList.get(i) != null) {
                        EnemyBulletList.get(i).draw();

                    }
                }
            }

            // ゲームオーバー
            if (players == 0) {
                GAME_MODE = GAME_OVER;
            }
            // ゲームクリア
            if (enemies == 0 && players != 0 && EnemyList.size() <= 0) {
                GAME_MODE = CLEAR;
            }

            if (playerList != null) {
                fill(255, 255, 255);
                textSize(30);
                textAlign(CENTER, CENTER);
                if (players != 0) {
                    text("HP : " + playerList.get(0).howHitPoint(), 0.2f * SCREEN_SIZE.x, 0.05f * SCREEN_SIZE.y);
                }
            }

            if (EnemyList != null) {
                fill(255, 255, 255);
                textSize(30);
                textAlign(CENTER, CENTER);
                if (enemies != 0) {
                    for (int i = 0; i < EnemyList.size(); i++) {
                        text("enemy : " + EnemyList.size(), 0.2f * SCREEN_SIZE.x, 0.1f * SCREEN_SIZE.y);
                    }
                }

            }
        }

    }

    // Processingのdrawメソッド
    public void draw() {
        move();
        react();
        doDraw();
    }

    // ProcessingのmousePressedメソッド
    public void mousePressed() {
    }

    public void mouseReleased() {

    }

    // ProcessingのkeyPressedメソッド
    public void keyPressed() {
        if (GAME_MODE == PLAY) {
            if (key == 's') { // スクリーンショットを保存
                saveFrame("Shooter.png");
            }

            if (key == 'q') { // 敵弾の一斉除去
                EnemyBulletList.clear(); // 敵の弾クリア
            }
        }
        if (key == ' ') { // next
            if (GAME_MODE < 2) {
                GAME_MODE++;
            } else {
                GAME_MODE = 0;
            }
        }
    }

    // ProcessingのkeyReleasedメソッド
    public void keyReleased() {
    }

    // Processingプログラムの起動
    public static void main(String[] args) {
        PApplet.main(Shooter.class.getName());
    }

}