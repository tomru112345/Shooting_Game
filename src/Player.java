// import java.util.ArrayList;
// import java.util.Random;
// import processing.core.PApplet;

// // 自機
// class Player extends Shooter.ShooterObject {

// Vector direction; // 移動方向

// // コンストラクタ
// Player(Vector position) {
// super(position);
// direction = new Vector(0, 0);
// }

// boolean isPlayer() {
// return true;
// }

// // 時間dt分の移動
// void step(float dt) {
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
// }

// // 敵enemyとの衝突判定
// boolean checkCollision(Enemy enemy) {
// return position.x > enemy.position.x - 10 && position.x < enemy.position.x +
// 10
// && position.y > enemy.position.y - 10 && position.y < enemy.position.y + 10;
// }

// // Processing画面への描画
// void draw() {
// fill(64, 192, 255);
// beginShape();
// vertex(position.x, position.y - 10);
// vertex(position.x + 10, position.y + 10);
// vertex(position.x - 10, position.y + 10);
// endShape(CLOSE);
// }

// }
