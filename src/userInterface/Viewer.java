/* ******************************************************
 * Project alpha - Composants logiciels 2015.
 * Copyright (C) 2015 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: userInterface/Viewer.java 2015-03-11 buixuan.
 * ******************************************************/
package userInterface;

import tools.HardCodedParameters;

import specifications.ViewerService;
import sun.security.x509.IssuingDistributionPointExtension;
import specifications.ReadService;
import specifications.RequireReadService;
import specifications.ShotService;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import metier.Alien;
import metier.Map;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

import algorithm.ShotBonus;

public class Viewer implements ViewerService, RequireReadService{
	private static final int spriteSlowDownRate=HardCodedParameters.spriteSlowDownRate;
	private static final double locationMainScoreJoueurX=HardCodedParameters.locationScoreJoueurX,
			locationMainScoreJoueurY=HardCodedParameters.locationScoreJoueurY,
			locationMainGameLevelX=HardCodedParameters.locationGameLevelX, 
			locationMainGameLevelY=HardCodedParameters.locationGameLevelY,
			defaultMainWidth=HardCodedParameters.defaultWidth,
			defaultMainHeight=HardCodedParameters.defaultHeight;
	private ReadService data;
	private ImageView heroesAvatar;
	private double xShrink,yShrink,shrink,xModifier,yModifier,heroesScale;
	private Image backgroundImage;
	private ImageView background;
	private Image gameOver;
	private ImageView gameOverImageView;
	private ProgressBar lifeBar;
	private Image starSprite; 
	private ImageView starLife;
	private Image headAlienKill; 
	private ImageView headAlienKillView;
	
	public Viewer(){}

	@Override
	public void bindReadService(ReadService service){
		data=service;
	}

	@Override
	public void init(){
		xShrink=1;
		yShrink=1;
		xModifier=0;
		yModifier=0;

		//Yucky hard-conding
		heroesAvatar = new ImageView(data.getHero().getImage());

		backgroundImage = new Image("file:src/images/background.jpg");
		background = new ImageView(backgroundImage);
		gameOver = new Image("file:src/images/youLose.jpg");
		gameOverImageView = new ImageView(gameOver);		
		starSprite = new Image("file:src/images/life-icon.png"); 
		headAlienKill = new Image("file:src/images/alien.png");
		headAlienKillView = new ImageView(headAlienKill);
	}

	@Override
	public Parent getPanel(){
		Group panel = new Group();

		//		if (data.getPlayer().getRemainingLives() <= 0) {
		//			
		//			
		//			return panel;
		//		}

		shrink=Math.min(xShrink,yShrink);
		xModifier=.01*shrink*defaultMainWidth;
		yModifier=.01*shrink*defaultMainHeight;


		background.setFitHeight((defaultMainHeight)*yShrink);
		background.setFitWidth((defaultMainWidth)*xShrink);
		//		background.autosize();
		//		background.setPreserveRatio(true);
		//Yucky hard-conding
		Map test = new Map();
		test.setAxeX(xModifier/4);
//		test.setAxeX(xModifier);
		test.setAxeY(yModifier);
		test.setWidth(-2*xModifier+shrink*defaultMainWidth);
		test.setHeight(-.2*shrink*yModifier+shrink*(defaultMainHeight-50));

		Rectangle map = test.draw();

		historiqueShoot();

		Text levelNumber = new Text("Level : " + data.getGame().getLevel());
		levelNumber.setFill(Color.WHITE);
		levelNumber.setFont(new Font(.05*shrink*defaultMainHeight));
		levelNumber.setTranslateX(shrink*locationMainGameLevelX+shrink*xModifier);
		levelNumber.setTranslateY(shrink*locationMainGameLevelY+shrink*20);
		
		Text score = new Text(""+data.getGame().getCurrentScore());
		score.setFill(Color.WHITE);
		score.setFont(new Font(.05*shrink*defaultMainHeight)); 
		score.setTranslateX(shrink*locationMainGameLevelX+shrink*xModifier);
		score.setTranslateY(shrink*locationMainGameLevelY+shrink*60);
		
		headAlienKillView.setFitHeight(headAlienKill.getHeight()*shrink);
		headAlienKillView.setPreserveRatio(true);
		headAlienKillView.setTranslateX(shrink*locationMainGameLevelX+shrink*xModifier);
		headAlienKillView.setTranslateY(shrink*locationMainGameLevelY+shrink*100-(headAlienKill.getHeight()/2)*shrink);
		
		Text enemyKilled = new Text("x" + data.getPlayer().getTotalKill());
		enemyKilled.setFill(Color.WHITE);
		enemyKilled.setFont(new Font(.05*shrink*defaultMainHeight)); 
		enemyKilled.setTranslateX(shrink*locationMainGameLevelX+shrink*headAlienKill.getWidth());
		enemyKilled.setTranslateY(shrink*locationMainGameLevelY+shrink*100+10*shrink);
		//	    levelNumber.setTranslateX(value);

		//int index=heroesAvatarViewportIndex/spriteSlowDownRate;
		heroesScale=data.getHero().getSizeY()*shrink/data.getHero().getImage().getHeight();
		heroesAvatar.setFitHeight(data.getHero().getSizeY()*shrink);
		heroesAvatar.setPreserveRatio(true);
		heroesAvatar.setTranslateX(shrink*data.getHero().getPosition().x+
				shrink*xModifier+
				-heroesScale*.5*data.getHero().getImage().getWidth());

		heroesAvatar.setTranslateY(shrink*data.getHero().getPosition().y+
				shrink*yModifier+
				-heroesScale*.5*data.getHero().getImage().getHeight()
				);


		//heroesAvatarViewportIndex=(heroesAvatarViewportIndex+1)%(heroesAvatarViewports.size()*spriteSlowDownRate);
		if(data.getPlayer().getRemainingLives() > 0){
			panel.getChildren().addAll(background, map, levelNumber, score,headAlienKillView, enemyKilled, heroesAvatar);

			ArrayList<Alien> aliens = data.getAliens();
			Alien alien;

			for (int i=0; i<aliens.size();i++){
				alien=aliens.get(i);
				ImageView imageAlien = new ImageView(alien.getImage());

				imageAlien.setFitHeight(alien.getSizeY()*shrink);
				imageAlien.setPreserveRatio(true);
				imageAlien.setTranslateX(shrink*alien.getPosition().x+
						shrink*xModifier+
						-heroesScale*.5*alien.getImage().getWidth());

				imageAlien.setTranslateY(shrink*alien.getPosition().y+
						shrink*yModifier+
						-heroesScale*.5*alien.getImage().getHeight()
						);

				for (int j =0; j<alien.getListShot().size();j++){
					double radius=2*Math.min(shrink*4,shrink*4);
					Circle shoot = new Circle(radius,  Color.BLUE);
					shoot.setEffect(new Lighting());
					shoot.setTranslateX(shrink*(alien.getListShot().get(j).getPosition().x+radius));
					shoot.setTranslateY(shrink*alien.getListShot().get(j).getPosition().y);
					panel.getChildren().add(shoot);
				}

				historiqueAlienShoot(alien);

				panel.getChildren().add(imageAlien);
			}

			for (int i =0; i<data.getHero().getListShot().size();i++){
				double radius=2*Math.min(shrink*4,shrink*4);
				Circle shoot = new Circle(radius,  Color.YELLOW);
				shoot.setEffect(new Lighting());
				shoot.setTranslateX(shrink*(data.getHero().getListShot().get(i).getPosition().x+radius));
				shoot.setTranslateY(shrink*data.getHero().getListShot().get(i).getPosition().y);
				panel.getChildren().add(shoot);
			}

			if (data.getBonusService() != null) {
				double radius=2*Math.min(shrink*4,shrink*4);
				Circle bonusIcon = new Circle(radius);
				bonusIcon.setFill(Color.GREEN);
				if (data.getBonusService() instanceof ShotBonus) {
					bonusIcon.setFill(Color.RED);
				}
				bonusIcon.setTranslateX(shrink*(data.getBonusService().getPosition().x+radius));
				bonusIcon.setTranslateY(shrink*data.getBonusService().getPosition().y);
				bonusIcon.setEffect(new Lighting());
				panel.getChildren().add(bonusIcon);
			}
			
			//vies restantes
			int lives = data.getPlayer().getRemainingLives();
			while (lives > 0) {
				starLife = new ImageView(starSprite);
				starLife.setFitHeight(starSprite.getWidth()*shrink);
				starLife.setPreserveRatio(true);
				starLife.setTranslateY(shrink*650+shrink*xModifier+-heroesScale*.5*starSprite.getWidth());  
				starLife.setTranslateX(shrink*(lives * 30)+shrink*yModifier+-heroesScale*.5*starSprite.getHeight());
				panel.getChildren().add(starLife);
				lives--;
			}

			//Barre de progression  
			double progressLife = (data.getHero().getLife()*0.1)/(HardCodedParameters.heroesHealth*0.1);
//			System.out.println("current life " + data.getHero().getLife());
//			System.out.println("remaining lives " + data.getPlayer().getRemainingLives());
//	        System.out.println("progressLife " +progressLife);
			lifeBar = new ProgressBar(progressLife);
			lifeBar.autosize();
	        lifeBar.setTranslateX(shrink*data.getHero().getPosition().x+  
	            shrink*xModifier+  
	            -heroesScale*.5*data.getHero().getImage().getWidth() - 25);  
	        lifeBar.setTranslateY(shrink*data.getHero().getPosition().y+  
	            shrink*yModifier+  
	            -heroesScale*.5*data.getHero().getImage().getHeight() + 35);
	        panel.getChildren().add(lifeBar);
	        
	        
		}else{
			gameOverImageView.setFitHeight(data.getMap().getHeight());
			gameOverImageView.setFitWidth(data.getMap().getWidth());
			gameOverImageView.setPreserveRatio(true);
			gameOverImageView.setEffect(new Lighting());
			gameOverImageView.setTranslateX(data.getMap().getAxeX());
			gameOverImageView.setTranslateY(data.getMap().getAxeY());
			panel.getChildren().add(gameOverImageView);
		}
		
        

		return panel;
	}

	@Override
	public void setMainWindowWidth(double width){
		xShrink=width/defaultMainWidth;
	}

	@Override
	public void setMainWindowHeight(double height){
		yShrink=height/defaultMainHeight;
	}

	private void historiqueShoot(){		
		for (int i =0; i<data.getHero().getListShot().size();i++){
			if (data.getHero().getListShot().get(i).getPosition().y<0){
				data.getHero().getListShot().remove(i);
			}
			else
				data.getHero().getListShot().get(i).getPosition().y= data.getHero().getListShot().get(i).getPosition().y-data.getHero().getShotSpeedRate();
		}
	}
	private void historiqueAlienShoot(Alien alien) {
		for (int i =0; i<alien.getListShot().size();i++){
			if (alien.getListShot().get(i).getPosition().y > data.getMap().getHeight()){
				alien.getListShot().remove(i);
			} else {
				alien.getListShot().get(i).getPosition().x += alien.getListShot().get(i).getFireX()*0.01;
				alien.getListShot().get(i).getPosition().y += alien.getListShot().get(i).getFireY()*0.01;				
			}
		}
	}

}
