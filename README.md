# ImageInputStream
_High-performance InputStream implementations for image stream handling._

ImageInputStream provides a set of hand-crafted image loaders
* reduced functionality for maximum performance
* designed for handling image streams or sequences elegantly
* dependency free + simple, readable code
* planned support for PNM (PPM, PGM) and PNG (RGBA only)
* ...

**Basic Image Loading**

    FileInputStream fis = new FileInputStream("test.ppm");
    BufferedImage b = new PPMInputStream(fis).readFrame().getImage();


**H264-compressed WebCam Streaming via SSH across machines**

    //Video decoder setup
    String cmd[] = { "/bin/bash", "-c", "ssh username@remote.local "+
        "/usr/local/bin/ffmpeg -f avfoundation -i 0 -vcodec h264 -f m4v - | "+ 
        "/usr/local/bin/ffmpeg -i - -f image2pipe -vcodec ppm -" };
    Process p = Runtime.getRuntime().exec(cmd);

    try (PNMInputStream pis = new PPMInputStream(p.getInputStream())) {
        //ImageInputStream setup
        ImageSource is = pis.readFrame();
        BufferedImage pbi = is.getImage();

        //UI setup
        JFrame f = new JFrame();
        JLabel l = new JLabel(new ImageIcon(pbi));
        f.setContentPane(l);
        f.setVisible(true);
        f.pack();

        //Frame loop
        for (is = pis.readFrame(); is!=null; l.repaint(), is = pis.readFrame() )
            is.getImage(pbi);
    }


## Release
_The current state of the project is to be considered pre-1.0 or almost-1.0._

In order to include this library to your project you have to

1. clone, build and install it

        git clone [...]/ImageInputStream.git && cd ImageInputStream && mvn compile install


2. modify the dependency declaration of your project (e.g. your maven **pom.xml**)

        <dependencies>
            ...
            <dependency>
                <groupId>de.dualuse</groupId>
                <artifactId>ImageInputStream</artifactId>
                <version>LATEST</version>
            </dependency>
            ...
        </dependencies>

3. enjoy




