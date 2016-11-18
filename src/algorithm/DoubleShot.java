package algorithm;

import metier.Starship;
import specifications.ShotService;
import tools.Position;

public class DoubleShot implements ShotService{

	@Override
	public void fire(Starship starship) {
		starship.getListShot().add(new Position((starship.getPosition().x- (starship.getSizeX()/2))+16, starship.getPosition().y));
		starship.getListShot().add(new Position((starship.getPosition().x+ (starship.getSizeX()/2))-16, starship.getPosition().y));
	}
	


}
