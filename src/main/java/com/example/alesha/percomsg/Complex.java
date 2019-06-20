package com.example.alesha.percomsg;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Original Author: Zaire Ali
 *      https://github.com/zaireali649/GestureDetection
 */
public class Complex {

    private final double re; // the real part
    private final double im; // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag)
    {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking com.example.alesha.percomsg.Complex object
    public String toString()
    {
        if (im == 0)
            return re + "";
        if (re == 0)
            return im + "i";
        if (im < 0)
            return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude and angle/phase/argument
    public double abs()
    {
        return Math.hypot(re, im);
    } // Math.sqrt(re*re + im*im)

    public double phase()
    {
        return Math.atan2(im, re);
    } // between -pi and pi

    // return a new com.example.alesha.percomsg.Complex object whose value is (this + b)
    public Complex plus(Complex b)
    {
        Complex a = this; // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is (this - b)
    public Complex minus(Complex b)
    {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is (this * b)
    public Complex times(Complex b)
    {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha)
    {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the conjugate of this
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the reciprocal of this
    public Complex reciprocal()
    {
        double scale = re * re + im * im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re()
    {
        return re;
    }

    public double im()
    {
        return im;
    }

    // return a / b
    public Complex divides(Complex b)
    {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the complex exponential of
    // this
    public Complex exp()
    {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re)
                * Math.sin(im));
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the complex sine of this
    public Complex sin()
    {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re)
                * Math.sinh(im));
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the complex cosine of this
    public Complex cos()
    {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re)
                * Math.sinh(im));
    }

    // return a new com.example.alesha.percomsg.Complex object whose value is the complex tangent of
    // this
    public Complex tan()
    {
        return sin().divides(cos());
    }

    // a static version of plus
    public static Complex plus(Complex a, Complex b)
    {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x)
    {
        int N = x.length;

        // base case
        if (N == 1)
            return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0)
        {
            throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++)
        {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd = even; // reuse the array
        for (int k = 0; k < N / 2; k++)
        {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++)
        {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x)
    {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++)
        {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++)
        {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++)
        {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

    // display an array of com.example.alesha.percomsg.Complex numbers to standard output
    public static void show(Complex[] x, String title)
    {
        System.out.println(title);
        for (int i = 0; i < x.length; i++)
        {
            System.out.println(x[i]);
        }
        System.out.println();
    }

    public static Complex[] getfft(double[] a)
    {
        Complex[] w = new Complex[a.length];
        for (int i = 0; i < w.length; i++)
        {
            w[i] = new Complex(a[i], 0);

        }

        // FFT of original data
        Complex[] x = fft(w);



        return x;
    }

    public static double sigEnergy(Complex[] x)
    {
        double b = 0.0;
        for (int z = 0; z < x.length; z++)
        {
            b = b + Math.pow(Math.pow((Math.pow(x[z].re,2) + Math.pow(x[z].im,2)),.5),2);

        }

        b = BigDecimal.valueOf((1.0/128.0) * b).setScale(4, RoundingMode.HALF_UP).doubleValue();



        return b;
    }

    public static double getabsolute(Complex x)
    {
        return Math.pow((Math.pow(x.re, 2) + Math.pow(x.im, 2)), .5);

    }

    public static double[] fftfreq(int n, double d)
    {
        double[] fftfreq = new double[n];
        fftfreq[0] = 0;

        if (n % 2 == 0)
        {
            //System.out.println("even");
            for( int i = 1; i < n/2; i++)
            {
                fftfreq[i] = i/(n*d);
            }
            for( int i = n/2; i < n; i++)
            {
                fftfreq[i] = -(n-i)/(n*d);
            }

        }
        else
        {
            //System.out.println("odd");
            for( int i = 1; i <= (n-1)/2; i++)
            {
                fftfreq[i] = i/(n*d);
            }
            for( int i = ((n-1)/2)+1; i < n; i++)
            {
                fftfreq[i] = -(n-i)/(n*d);
            }
        }


        return fftfreq;
    }


}
