package org.simbrain.world.threedee;

import javax.swing.ImageIcon;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

public class Terrain extends MultipleViewElement<TerrainBlock> {
    private MidPointHeightMap heightMap = new MidPointHeightMap(64, 1f);
    private final TerrainBlock heightBlock = create();
    
    public float getHeight(Vector3f location) {
        return heightBlock.getHeight(location);
    }
    
    @Override
    protected TerrainBlock create() {
        Vector3f terrainScale = new Vector3f(4, 0.0575f, 4);
        return new TerrainBlock("Terrain", heightMap.getSize(), terrainScale,
            heightMap.getHeightMap(), new Vector3f(0, 0, 0), false);
    }
    
    @Override
    public void initSpatial(Renderer renderer, TerrainBlock block) {
        /* generate a terrain texture with 2 textures */
        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(getClass().getClassLoader()
            .getResource("grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(getClass().getClassLoader()
            .getResource("dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(getClass().getClassLoader()
            .getResource("highest.jpg")), 128, 255, 384);
        pt.createTexture(32);
       
        /* assign the texture to the terrain */
        TextureState ts = renderer.createTextureState();
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
            Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);
        
        ts.setTexture(t1, 0);
    
        block.setModelBound(new BoundingBox());
        block.updateModelBound();
        
        block.setRenderState(ts);
        block.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    }

    @Override
    public void updateSpatial(TerrainBlock block) {
        /* no implementation */
    }

    public Vector3f getDirection() {
        return new Vector3f(0,0,0);
    }

    public float getSpeed() {
        return 0f;
    }

    public void collision(Collision collision) {
        /* no implementation */
    }

    public SpatialData getSpatialData() {
        return null;
    }
}