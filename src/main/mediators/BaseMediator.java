package mediators;

import proxylet.Proxylet;

/**
 * Created by ahmed on 7/18/17.
 */
public abstract class BaseMediator extends Proxylet {
    public BaseMediator(Class<?> childClass) {
        super(childClass);
    }
}
