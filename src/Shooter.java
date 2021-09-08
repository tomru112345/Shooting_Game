import java.util.ArrayList;
import java.util.Random;
import processing.core.PApplet;

// ゲーム本体
public class Shooter extends PApplet {
    static final Vector SCREEN_SIZE = new Vector(500, 750); // 画面サイズ
    static final Vector PLAYER_VELOCITY = new Vector(200, 200); // 自機の速度
    static final Vector BULLET_VELOCITY = new Vector(0, -500); // 弾の速度
    static final Vector ENEMY_BULLET_VELOCITY = new Vector(0, 500); // 弾の速度
    static final float TIME_STEP = 1 / 60.0f; // 1フレームの時間

    static final int MENU = 0; // メニュー
    static final int PLAY = 1; // ゲームプレイ
    static final int RETIRE = 2; // リタイア
    static final int CLEAR = 3; // クリア
    static final int GAME_OVER = 4; // ゲームオーバ
    int GAME_MODE = 0; // ゲームモード

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

        // コンストラクタ
        Player(Vector position) {
            super(position);
            direction = new Vector(0, 0);
        }

        boolean isPlayer() {
            return true;
        }

        // 時間dt分の移動
        void step(float dt) {
            // Vector velocity = Shooter.PLAYER_VELOCITY.createScale(direction);
            // position.add(velocity.createScale(dt));
            // if (position.x < 10) {
            // position.x = 10;
            // } else if (position.x > Shooter.SCREEN_SIZE.x - 10) {
            // position.x = Shooter.SCREEN_SIZE.x - 10;
            // }
            // if (position.y < 0.5f * Shooter.SCREEN_SIZE.y + 10) {
            // position.y = 0.5f * Shooter.SCREEN_SIZE.y + 10;
            // } else if (position.y > Shooter.SCREEN_SIZE.y - 10) {
            // position.y = Shooter.SCREEN_SIZE.y - 10;
            // }
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
        }

        // 敵enemyとの衝突判定
        boolean checkCollision(Enemy enemy) {
            return position.x > enemy.position.x - 10 && position.x < enemy.position.x + 10
                    && position.y > enemy.position.y - 10 && position.y < enemy.position.y + 10;
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

        // コンストラクタ
        Enemy(Vector position, Vector velocity, float factor) {
            super(position);
            this.velocity = new Vector(velocity);
            center = position.x;
            this.factor = factor;
        }

        boolean isEnemy() {
            return true;
        }

        // 時間dt分の移動
        void step(float dt) {
            Vector accel = new Vector(-factor * (position.x - center), 0);
            Vector dv = accel.createScale(dt);
            velocity.add(dv);
            position.add(velocity.createScale(dt));
            if (position.y > Shooter.SCREEN_SIZE.y + 10) {
                position.y = -10;
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

        // コンストラクタ
        EnemyBullet(Vector position, Vector velocity) {
            super(position);
            this.velocity = new Vector(velocity);
        }

        boolean isEnemyBullet() {
            return true;
        }

        // 時間dt分の移動
        void step(float dt) {
            position.add(velocity.createScale(dt));
            if (position.y < -10) {
                removeFlag = true;
            }
        }

        // Processing画面への描画
        void draw() {
            // fill(255, 255, 128);
            fill(255, 64, 128);
            ellipse(position.x + 2, position.y + 10, 10, 10);
        }

    }

    boolean bulletShootFlag; // 弾を撃つか

    boolean EnemyBulletShootFlag; // 敵弾を撃つか

    ArrayList<ShooterObject> playerList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> EnemyList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> playerBulletList = new ArrayList<ShooterObject>();
    ArrayList<ShooterObject> EnemyBulletList = new ArrayList<ShooterObject>();

    int count = 0;

    // 初期化
    void initialize() {
        playerList.clear();
        playerList.add(new Player(SCREEN_SIZE.createScale(new Vector(0.5f, 0.9f))));

        EnemyList.clear();
        playerBulletList.clear();
        EnemyBulletList.clear(); // 敵の弾クリア
        EnemyBulletShootFlag = false;

        bulletShootFlag = false;
        Random random = new Random();

        // 敵の位置・速度の初期値と揺れの係数をランダムに決定

        for (int i = 0; i < 1; i++) {
            // Vector enemyPosition = SCREEN_SIZE
            // .createScale(new Vector(0.9f * random.nextFloat() + 0.05f, 0.5f *
            // random.nextFloat()));
            Vector enemyPosition = SCREEN_SIZE.createScale(new Vector(0.5f, 0.2f));
            // Vector enemyVelocity = new Vector(1000 * (random.nextFloat() - 0.5f), 200 *
            // (random.nextFloat() + 0.5f));
            Vector enemyVelocity = new Vector(0, 0);
            float enemyFactor = 25 * (random.nextFloat() + 0.5f);

            EnemyList.add(new Enemy(enemyPosition, enemyVelocity, enemyFactor));

            EnemyBulletList.add(new EnemyBullet(EnemyList.get(0).position, ENEMY_BULLET_VELOCITY));

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

                if (count % 3600 == 0) {
                    EnemyBulletList.add(new EnemyBullet(EnemyList.get(0).position, ENEMY_BULLET_VELOCITY));
                }

            }
        }
        if (count % 3600 == 0) {
            count = 0;
        } else {
            count++;
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
                                    playerList.get(i).removeFlag = true;
                                    EnemyList.get(t).removeFlag = true;
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
                                    EnemyList.get(t).removeFlag = true;
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
                                    playerList.get(i).removeFlag = true;
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

                fill(255, 255, 255);
                textSize(30);
                textAlign(CENTER, CENTER);
                text(enemies, 0.1f * SCREEN_SIZE.x, 0.1f * SCREEN_SIZE.y);
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
            if (enemies == 0 && players != 0) {
                GAME_MODE = CLEAR;
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
        playerBulletList.add(new Bullet(playerList.get(0).position, BULLET_VELOCITY));
    }

    // ProcessingのkeyPressedメソッド
    public void keyPressed() {
        if (key == 's') { // スクリーンショットを保存
            saveFrame("Shooter.png");
        } else if (key == 'r') { // リスタート
            initialize();
        } else if (key == ' ') { // next
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