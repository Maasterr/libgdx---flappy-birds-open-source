package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.FlappyDemo;
import com.mygdx.game.sprites.Bird;
import com.mygdx.game.sprites.Tube;

/**
 * Created by Bausch on 04.08.2017.
 */

public class PlayState extends State {

    public static final int TUBE_SPACING = 125; //width between tubes
    public static final int TUBE_COUNT = 4; // 4 sets of tubes
    public static final int GROUND_Y_OFFSET = -45;
    private Bird bird;
    private Texture background,ground;
    private Vector2 groundPos1, groundPos2;
    public BitmapFont font;

    private static long counter_tubes=0;
    private static long counter_time=TimeUtils.millis();


    private Array<Tube> tubes;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50,300);
        camera.setToOrtho(false,FlappyDemo.WIDTH/2,FlappyDemo.HEIGHT/2);
        background = new Texture("bg.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(camera.position.x - camera.viewportWidth/2,GROUND_Y_OFFSET);
        groundPos2 = new Vector2(camera.position.x - camera.viewportWidth/2 + ground.getWidth(),GROUND_Y_OFFSET);
        counter_time=TimeUtils.millis();
        font = new BitmapFont();


        tubes = new Array<Tube>();

            for (int i = 1; i <= TUBE_COUNT; i++) {

                    tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));


            }

    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        bird.update(dt);
        camera.position.x = bird.getPosition().x+80;


        counter_tubes = (TimeUtils.millis()-counter_time)/1750;
        for(int i =0; i<tubes.size;i++){
            Tube tube=tubes.get(i);

            if(camera.position.x - (camera.viewportWidth/2)>tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                    tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                 }
            if(tube.colides(bird.getBounds())){
                counter_tubes=0;
                gsm.set(new GameOver(gsm));
            }
        }
        camera.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);

        counter_tubes +=Gdx.graphics.getDeltaTime();
        sb.begin();

        sb.draw(background,camera.position.x - (camera.viewportWidth/2),0);
        sb.draw(bird.getBird(),bird.getPosition().x, bird.getPosition().y);
        for(Tube tube: tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground,groundPos1.x, groundPos1.y);
        sb.draw(ground,groundPos2.x, groundPos2.y);
        font.draw(sb,"Count: " + counter_tubes,camera.position.x-115,395);
        sb.end();


        }

    @Override
    public void dispose() {
        background.dispose();
        bird.dispose();
        ground.dispose();
        for(Tube tube: tubes) {
            tube.dispose();
            System.out.println("Playstate dispoced");

        }

    }

    private void updateGround(){
        if(camera.position.x - (camera.viewportWidth/2)>groundPos1.x+ground.getWidth())
            groundPos1.add(ground.getWidth()*2,0);
        if(camera.position.x - (camera.viewportWidth/2)>groundPos2.x+ground.getWidth())
            groundPos2.add(ground.getWidth()*2,0);


    }
}
