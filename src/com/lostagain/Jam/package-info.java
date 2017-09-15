/**
 * Intended to be the location of all Jam engine related stuff.
 * 
 * A few important classes;
 * com.lostagain.Jam.JamCore.java - settings and management of the Jam game
 * com.lostagain.Jam.RequiredImplementations.java - stores and prevides reference too all the essential functions specific implementations must give the core engine
 * (ie, a GWT engine will give this class its own functions to manage the screen, the JAMcore can then refer to these methods without knowing anything about how they work)
 * com.lostagain.Jam.OptionalImplementations.java - same as above, but for optional things that not all implementations have to support
 * 
 * (Note; All things in the JAMCore project are intended to be non-visual generic java stuff, not actual implementations 
 * of game visuals)
 */
/**
 * @author darkflame
 *
 */
package com.lostagain.Jam;	