package org.adw.launcher;

import java.util.LinkedList;
import java.util.List;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Provides methods and constructs for handling themes with an icon shader.
 * Create parser from theme/res/xml/shader.xml
 * Compile with CompiledIconShader parseXml(XmlResourceParser xpp)
 * Process icons with Drawable processIcon(Drawable icon, CompiledIconShader c)
 */
class IconShader {
    
    static enum IMAGE {
        ICON, BUFFER, OUTPUT
    }

    static enum MODE {
        NONE, WRITE, MULTIPLY, DIVIDE, ADD, SUBTRACT
    }

    static enum INPUT {
        AVERAGE, INTENSITY, CHANNEL, VALUE
    }
    
    static class CompiledIconShader {
        private List<Shader> shaders;
        
        CompiledIconShader(List<Shader> s) {
            shaders = s;
        }
        
        List<Shader> getShaderList() {
            return shaders;
        }
    }

    private static class Shader {
        MODE mode;
        IMAGE target;
        int targetChannel;

        IMAGE input;
        INPUT inputMode;
        int inputChannel;
        float inputValue;

        public Shader(MODE mode, IMAGE target, int targetChannel, IMAGE input,
                INPUT inputMode, int inputChannel, float inputValue) {
            this.mode = mode;
            this.target = target;
            this.targetChannel = targetChannel;
            this.input = input;
            this.inputMode = inputMode;
            this.inputChannel = inputChannel;
            this.inputValue = inputValue;
        }
    }

    static CompiledIconShader parseXml(XmlResourceParser xpp) {
        List<Shader> shaders = new LinkedList<Shader>();
        String a0, a1, a2;
        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG
                        && xpp.getName().compareTo("exec") == 0
                        && xpp.getAttributeCount() == 3) {

                    a0 = xpp.getAttributeValue(0);
                    a1 = xpp.getAttributeValue(1);
                    a2 = xpp.getAttributeValue(2);
                    shaders.add(createShader(a0, a1, a2));
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            return null;
        }
        return new CompiledIconShader(shaders);
    }

    private static Shader createShader(String targetStr, String modeStr, String inputStr) {
        MODE mode = MODE.NONE;
        IMAGE target = IMAGE.OUTPUT;
        int targetChannel = 0;
        IMAGE input = IMAGE.ICON;
        INPUT inputMode = INPUT.CHANNEL;
        int inputChannel = 0;
        float inputValue = 0;
        try {
            switch (modeStr.charAt(0)) {
            case 'W':
                mode = MODE.WRITE;
                break;
            case 'M':
                mode = MODE.MULTIPLY;
                break;
            case 'D':
                mode = MODE.DIVIDE;
                break;
            case 'A':
                mode = MODE.ADD;
                break;
            case 'S':
                mode = MODE.SUBTRACT;
                break;
            default:
                throw (new Exception());
            }

            switch (targetStr.charAt(0)) {
            case 'B':
                target = IMAGE.BUFFER;
                break;
            case 'O':
                target = IMAGE.OUTPUT;
                break;
            default:
                throw (new Exception());
            }
            switch (targetStr.charAt(1)) {
            case 'A':
                targetChannel = 0;
                break;
            case 'R':
                targetChannel = 1;
                break;
            case 'G':
                targetChannel = 2;
                break;
            case 'B':
                targetChannel = 3;
                break;
            default:
                throw (new Exception());
            }

            boolean isValue = false;
            switch (inputStr.charAt(0)) {
            case 'I':
                input = IMAGE.ICON;
                break;
            case 'B':
                input = IMAGE.BUFFER;
                break;
            case 'O':
                input = IMAGE.OUTPUT;
                break;
            default:
                inputValue = new Float(inputStr);
                isValue = true;
                inputMode = INPUT.VALUE;
                ;
            }
            if (!isValue)
                switch (inputStr.charAt(1)) {
                case 'A':
                    inputChannel = 0;
                    break;
                case 'R':
                    inputChannel = 1;
                    break;
                case 'G':
                    inputChannel = 2;
                    break;
                case 'B':
                    inputChannel = 3;
                    break;
                case 'I':
                    inputMode = INPUT.INTENSITY;
                    break;
                case 'H':
                    inputMode = INPUT.AVERAGE;
                    break;
                default:
                    throw (new Exception());
                }
        } catch (Exception e) {
        }

        return new Shader(mode, target, targetChannel, input, inputMode,
                inputChannel, inputValue);
    }

    static Drawable processIcon(Drawable icon_d, CompiledIconShader c) {
        List<Shader> shaders = c.getShaderList();
        //main.text = "";
        BitmapDrawable icon_bd = (BitmapDrawable) icon_d;
        Bitmap icon_bitmap = icon_bd.getBitmap();
        int width = icon_bitmap.getWidth();
        int height = icon_bitmap.getHeight();
        int length = width * height;
        int[] pixels = new int[length];
        float[][] icon = new float[4][length];
        float[][] buffer = new float[4][length];
        float[][] output = new float[4][length];
        Float icon_average = null;
        Float buffer_average = null;
        Float output_average = null;
        float[] icon_intensity = null;
        float[] buffer_intensity = null;
        float[] output_intensity = null;

        // //main.text += "Init...";
        icon_bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < length; i++) {
            icon[3][i] = pixels[i] & 0x000000FF;
            icon[2][i] = (pixels[i] >> 8) & 0x000000FF;
            icon[1][i] = (pixels[i] >> 16) & 0x000000FF;
            icon[0][i] = (pixels[i] >> 24) & 0x000000FF;
        }
        // //main.text += "done\n";
        /*
         * for (int i=48*10; i<48*11; i++) { //main.text += icon[0][i] + " "; }
         */

        float inputValue = 0;
        float[] inputArray = null;
        float[] targetArray = null;
        for (Shader s : shaders) {

            //main.text += main.ts(s) + "\n";

            if (s.mode == MODE.NONE)
                continue;

            // determine input
            if (s.inputMode == INPUT.AVERAGE) {
                //main.text += "input average " + s.input + "\n";
                switch (s.input) {
                case ICON:
                    if (icon_average == null) {
                        //main.text += "makeaverage\n";
                        icon_average = getAverage(icon, length);
                    }
                    inputValue = icon_average;
                    break;
                case BUFFER:
                    if (buffer_average == null) {
                        buffer_average = getAverage(buffer, length);
                    }
                    inputValue = buffer_average;
                    break;
                case OUTPUT:
                    if (output_average == null) {
                        output_average = getAverage(output, length);
                    }
                    inputValue = output_average;
                    break;
                }
            }
            if (s.inputMode == INPUT.INTENSITY) {
                switch (s.input) {
                case ICON:
                    if (icon_intensity == null) {
                        icon_intensity = getIntensity(icon, length);
                    }
                    inputArray = icon_intensity;
                    break;
                case BUFFER:
                    if (buffer_intensity == null) {
                        buffer_intensity = getIntensity(buffer, length);
                    }
                    inputArray = buffer_intensity;
                    break;
                case OUTPUT:
                    if (output_intensity == null) {
                        output_intensity = getIntensity(output, length);
                    }
                    inputArray = output_intensity;
                    break;
                }
            }
            if (s.inputMode == INPUT.CHANNEL) {
                switch (s.input) {
                case ICON:
                    inputArray = icon[s.inputChannel];
                    break;
                case BUFFER:
                    inputArray = buffer[s.inputChannel];
                    break;
                case OUTPUT:
                    inputArray = output[s.inputChannel];
                    break;
                }
            }
            if (s.inputMode == INPUT.VALUE) {
                inputValue = s.inputValue;
            }

            // //main.text += "done input\n";

            // determine target
            if (s.target == IMAGE.BUFFER) {
                targetArray = buffer[s.targetChannel];
            }
            if (s.target == IMAGE.OUTPUT) {
                targetArray = output[s.targetChannel];
                //main.text += "target output\n";
            }

            // //main.text += "done target\n";

            // write to target
            switch (s.mode) {
            case WRITE:
                if (s.inputMode == INPUT.AVERAGE || s.inputMode == INPUT.VALUE) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] = inputValue;
                }
                if (s.inputMode == INPUT.INTENSITY
                        || s.inputMode == INPUT.CHANNEL) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] = inputArray[i];
                }
                break;
            case MULTIPLY:
                if (s.inputMode == INPUT.AVERAGE || s.inputMode == INPUT.VALUE) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] *= inputValue;
                }
                if (s.inputMode == INPUT.INTENSITY
                        || s.inputMode == INPUT.CHANNEL) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] *= inputArray[i];
                }
                break;
            case DIVIDE:
                if (s.inputMode == INPUT.AVERAGE || s.inputMode == INPUT.VALUE) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] /= inputValue;
                }
                if (s.inputMode == INPUT.INTENSITY
                        || s.inputMode == INPUT.CHANNEL) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] /= inputArray[i];
                }
                break;
            case ADD:
                if (s.inputMode == INPUT.AVERAGE || s.inputMode == INPUT.VALUE) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] += inputValue;
                }
                if (s.inputMode == INPUT.INTENSITY
                        || s.inputMode == INPUT.CHANNEL) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] += inputArray[i];
                }
                break;
            case SUBTRACT:
                if (s.inputMode == INPUT.AVERAGE || s.inputMode == INPUT.VALUE) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] -= inputValue;
                }
                if (s.inputMode == INPUT.INTENSITY
                        || s.inputMode == INPUT.CHANNEL) {
                    for (int i = 0; i < length; i++)
                        targetArray[i] -= inputArray[i];
                }
                break;
            }

            //main.text += "done write\n";
            /*
             * for (int i=480; i<510; i++) { //main.text += targetArray[i] + " ";
             * }
             */

            // invalidate average/intensity
            switch (s.target) {
            case BUFFER:
                buffer_average = null;
                buffer_intensity = null;
                break;
            case OUTPUT:
                output_average = null;
                output_intensity = null;
                break;
            }

            // //main.text += "done inval\n";
        }

        // //main.text += "done shading\n";
        for (int i = 480; i < 510; i++) {
            //main.text += output[0][i] + " ";
        }

        int a, r, g, b;
        for (int i = 0; i < length; i++) {
            a = (int) output[0][i];
            r = (int) output[1][i];
            g = (int) output[2][i];
            b = (int) output[3][i];

            a = a > 255 ? 255 : a < 0 ? 0 : a;
            r = r > 255 ? 255 : r < 0 ? 0 : r;
            g = g > 255 ? 255 : g < 0 ? 0 : g;
            b = b > 255 ? 255 : b < 0 ? 0 : b;

            a <<= 8;
            a |= r;
            a <<= 8;
            a |= g;
            a <<= 8;
            a |= b;
            pixels[i] = a;
        }

        // //main.text += "done writeback\n";

        Bitmap outputBitmap = Bitmap.createBitmap(pixels, width, height,
                icon_bitmap.getConfig());
        BitmapDrawable outputBD = new BitmapDrawable(outputBitmap);
        return outputBD;
    }
    
    private static float getAverage(float[][] array, int length) {
        double average = 0;
        double total = 0;
        for (int i = 0; i < length; i++) {
            average += array[0][i] * (array[1][i] + array[2][i] + array[3][i])
                    / 3;
            total += array[0][i];
        }
        average /= total;
        //main.text += "final average " + average + "\n";
        return (float) average;
    }

    private static float[] getIntensity(float[][] array, int length) {
        float[] intensity = new float[length];
        for (int i = 0; i < length; i++)
            intensity[i] = (array[1][i] + array[2][i] + array[3][i]) / 3;
        //main.text += "got intensity\n";
        return intensity;
    }
}