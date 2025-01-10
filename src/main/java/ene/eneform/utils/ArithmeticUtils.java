/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

/**
 *
 * @author Simon
 */
public class ArithmeticUtils {
    
    

public static int gcd(int a, int b)
{
    while (b > 0)
    {
        int temp = b;
        b = a % b; // % is remainder
        a = temp;
    }
    return a;
}

public static int gcd(int[] input)
{
    int result = input[0];
    for(int i = 1; i < input.length; i++) 
    {
        result = gcd(result, input[i]);
    }
    return result;
}

public static int lcm(int a, int b)
{
    return a * (b / gcd(a, b));
}

public static int lcm(int[] input)
{
    int result = input[0];
    for(int i = 1; i < input.length; i++) 
    {
        result = lcm(result, input[i]);
    }
    return result;
}

public static double round(double dInput, int nPlaces)
{
    double dScale = 1d * Math.pow(10, nPlaces);
    return (double)Math.round(dInput * dScale) / dScale;
}

}
