package at.tugraz.oop2.shared;

public abstract class Set{
    private SimpleImage image;

    protected FractalRenderOptions renderingOptions;

    public void setRenderingOptions(FractalRenderOptions rOptions){
        this.renderingOptions = rOptions;
        this.image = new SimpleImage(3, renderingOptions.getWidth().get(), renderingOptions.getHeight().get());
    }
    protected Set(){}
    public SimpleImage getSimpleImage() { return  image; }
    public FractalRenderOptions getFractalRenderOptions() { return renderingOptions; }

    public void updateImage() {
        image = new SimpleImage(getFractalRenderOptions().getWidth().get(), getFractalRenderOptions().getHeight().get());
    }
}
