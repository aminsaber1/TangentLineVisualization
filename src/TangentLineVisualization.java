import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class TangentLineVisualization extends JPanel {

    private static final int SCALE = 45;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public TangentLineVisualization() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        drawGridAndAxes(g2, cx, cy);
        drawParametricCurve(g2, cx, cy, t -> t, t -> t * t, Color.BLUE, -3.5, 3.5);
        drawParametricCurve(g2, cx, cy, t -> -t, t -> t * t, new Color(220, 0, 0), -3.5, 3.5);
        drawParametricCurve(g2, cx, cy, t -> (t * Math.cos(t)) / 3.0, t -> (t * Math.sin(t)) / 3.0, new Color(0, 200, 0), -10, 10);
        drawTangentLine(g2, cx, cy, 2.0, 4.0, 4.0, Color.BLUE);
        drawPoint(g2, cx, cy, 2.0, 4.0, Color.BLUE);
        drawTangentLine(g2, cx, cy, -2.0, 4.0, -4.0, new Color(220, 0, 0));
        drawPoint(g2, cx, cy, -2.0, 4.0, new Color(220, 0, 0));
        g2.setColor(new Color(0, 200, 0));
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5, 5}, 0);
        g2.setStroke(dashed);
        g2.drawLine(cx - 100, cy - (int)(0.6 * SCALE), cx + 100, cy - (int)(0.6 * SCALE));
        drawLegend(g2);
    }

    private void drawGridAndAxes(Graphics2D g2, int cx, int cy) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

        for (int i = -20; i <= 20; i++) {
            if (i == 0) continue;

            int x = cx + i * SCALE;
            int y = cy - i * SCALE;
            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(x, 0, x, getHeight());
            g2.drawLine(0, y, getWidth(), y);
            g2.setColor(Color.GRAY);
            g2.drawString(String.valueOf(i), x - 3, cy + 15);
            g2.drawString(String.valueOf(i), cx + 5, y + 4);
        }

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(0, cy, getWidth(), cy);
        g2.drawLine(cx, 0, cx, getHeight());
    }

    interface MathFunction {
        double f(double t);
    }

    private void drawParametricCurve(Graphics2D g2, int cx, int cy,
                                     MathFunction xFunc, MathFunction yFunc,
                                     Color color, double tStart, double tEnd) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f));

        Path2D.Double path = new Path2D.Double();
        double step = 0.02;
        boolean firstPoint = true;

        for (double t = tStart; t <= tEnd; t += step) {
            double mx = xFunc.f(t);
            double my = yFunc.f(t);

            double px = cx + mx * SCALE;
            double py = cy - my * SCALE;

            if (firstPoint) {
                path.moveTo(px, py);
                firstPoint = false;
            } else {
                path.lineTo(px, py);
            }
        }
        g2.draw(path);
    }

    private void drawTangentLine(Graphics2D g2, int cx, int cy, double x0, double y0, double slope, Color color) {
        g2.setColor(color);
        float[] dash = {6.0f, 6.0f};
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        double xLeft = x0 - 4.0;
        double yLeft = slope * (xLeft - x0) + y0;
        double xRight = x0 + 4.0;
        double yRight = slope * (xRight - x0) + y0;
        int x1 = cx + (int)(xLeft * SCALE);
        int y1 = cy - (int)(yLeft * SCALE);
        int x2 = cx + (int)(xRight * SCALE);
        int y2 = cy - (int)(yRight * SCALE);

        g2.drawLine(x1, y1, x2, y2);
    }

    private void drawPoint(Graphics2D g2, int cx, int cy, double x, double y, Color color) {
        int px = cx + (int)(x * SCALE);
        int py = cy - (int)(y * SCALE);
        int r = 4;
        g2.setColor(color);
        g2.fillOval(px - r, py - r, 2 * r, 2 * r);
    }

    private void drawLegend(Graphics2D g2) {
        int xStart = 20;
        int yStart = 20;
        int width = 230;
        int height = 110;
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRect(xStart, yStart, width, height);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = 18;
        int currentY = yStart + 15;
        drawLegendItem(g2, xStart + 5, currentY, Color.BLUE, "Curve 1: x=t, y=t^2");
        currentY += lineHeight;
        drawLegendItem(g2, xStart + 5, currentY, new Color(220, 0, 0), "Curve 2: x=-t, y=t^2");
        currentY += lineHeight;
        drawLegendItem(g2, xStart + 5, currentY, new Color(0, 200, 0), "Curve 3: x=t*cos(t)/3, y=t*sin(t)/3");
        currentY += lineHeight + 5;
        g2.setColor(Color.BLACK);
        g2.drawString("Tangent point at t = 2.0", xStart + 5, currentY);
        currentY += lineHeight;
        g2.drawString("Dashed lines = Tangent lines", xStart + 5, currentY);
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color c, String text) {
        g2.setColor(c);
        g2.fillRect(x, y - 8, 12, 8);
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 18, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tangent Line Visualization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TangentLineVisualization());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
