/* ******************************************************
 * Project alpha - Composants logiciels 2015.
 * Copyright (C) 2015 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: userInterface/Viewer.java 2015-03-11 buixuan.
 * ******************************************************/
package userInterface;

import tools.HardCodedParameters;

import specifications.ViewerService;
import specifications.ReadService;
import specifications.RequireReadService;
import specifications.PhantomService;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import metier.Map;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Viewer implements ViewerService, RequireReadService{
	private static final double defaultMainWidth=HardCodedParameters.defaultWidth,
			defaultMainHeight=HardCodedParameters.defaultHeight;
	private ReadService data;
	private ImageView heroesAvatar;
	private double xShrink,yShrink,shrink,xModifier,yModifier,heroesScale;

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
	}

	@Override
	public Parent getPanel(){
		shrink=Math.min(xShrink,yShrink);
		xModifier=.01*shrink*defaultMainHeight;
		yModifier=.01*shrink*defaultMainHeight;

		//Yucky hard-conding
		Rectangle map = new Map(xModifier, yModifier, -2*xModifier+shrink*defaultMainWidth, -.2*shrink*defaultMainHeight+shrink*defaultMainHeight).draw();

		data.getMap().setAxeX(xModifier);
		data.getMap().setAxeY(yModifier);
		data.getMap().setWidth(map.getWidth());
		data.getMap().setHeight(map.getHeight());
		historiqueShoot();

		Text greets = new Text(-0.1*shrink*defaultMainHeight+.5*shrink*defaultMainWidth,
				-0.1*shrink*defaultMainWidth+shrink*defaultMainHeight,
				"Round 1");
		greets.setFont(new Font(.05*shrink*defaultMainHeight));

		Text score = new Text(-0.1*shrink*defaultMainHeight+.5*shrink*defaultMainWidth,
				-0.05*shrink*defaultMainWidth+shrink*defaultMainHeight,
				"Score: "+data.getScore());
		score.setFont(new Font(.05*shrink*defaultMainHeight));

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
		Group panel = new Group();
		panel.getChildren().addAll(map,greets,score,heroesAvatar);

		ArrayList<PhantomService> phantoms = data.getPhantoms();
		PhantomService p;

		for (int i=0; i<phantoms.size();i++){
			p=phantoms.get(i);
			double radius=.5*Math.min(shrink*data.getPhantomWidth(),shrink*data.getPhantomHeight());
			Circle phantomAvatar = new Circle(radius,Color.rgb(255,156,156));
			phantomAvatar.setEffect(new Lighting());
			phantomAvatar.setTranslateX(shrink*p.getPosition().x+shrink*xModifier-radius);
			phantomAvatar.setTranslateY(shrink*p.getPosition().y+shrink*yModifier-radius);
			panel.getChildren().add(phantomAvatar);
		}

		for (int i =0; i<data.getHero().getListShot().size();i++){
			double radius=2*Math.min(shrink*4,shrink*4);
			Circle shoot = new Circle(radius,  Color.YELLOW);
			shoot.setEffect(new Lighting());
			shoot.setTranslateX(shrink*(data.getHero().getListShot().get(i).x+radius));
			shoot.setTranslateY(shrink*data.getHero().getListShot().get(i).y);
			panel.getChildren().add(shoot);
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
			if (data.getHero().getListShot().get(i).y<0){
				data.getHero().getListShot().remove(i);
			}
			else
				data.getHero().getListShot().get(i).y= data.getHero().getListShot().get(i).y-5;
		}
	}
}
