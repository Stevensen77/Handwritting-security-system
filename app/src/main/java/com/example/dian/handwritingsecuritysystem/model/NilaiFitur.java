package com.example.dian.handwritingsecuritysystem.model;

import android.util.Log;

public class NilaiFitur {
    public String nama;
    public String noHP;
    public String ktp;
    public int greyscale1;
    public double nilai_invariance;
    public double nilai_entropy;
    public double nilai_skewness;
    public double relative_smoothness;
    public double nilai_energy;
    public double nilai_contrast;

    public NilaiFitur(String nama, String noHP, String ktp, int greyscale1, double nilai_invariance, double nilai_entropy, double nilai_skewness, double relative_smoothness, double nilai_energy, double nilai_contrast)
    {
        Log.d("Constructor_jalan", String.valueOf(greyscale1));
        this.nama = nama;
        this.noHP = noHP;
        this.ktp = ktp;
        this.greyscale1 = greyscale1;
        this.nilai_invariance = nilai_invariance;
        this.nilai_entropy = nilai_entropy;
        this.nilai_skewness = nilai_skewness;
        this.relative_smoothness = relative_smoothness;
        this.nilai_energy = nilai_energy;
        this.nilai_contrast = nilai_contrast;
    }

/*
    public String getNama() {
        Log.d("ISI NAMA", nama);
        return nama; }
    public String getNoHP() { return noHP; }
    public String getKtp() { return ktp; }
    public int getGreyscale1() {
        Log.d("ISI ZZZZZZ", String.valueOf(greyscale1));
        return greyscale1; }
    public double getNilai_invariance() { return nilai_invariance; }
    public double getNilai_entropy() { return nilai_entropy; }
    public double getNilai_skewness() { return nilai_skewness; }
    public double getRelative_smoothness() { return relative_smoothness; }
    public double getNilai_energy() { return nilai_energy; }
    public double getNilai_contrast() { return nilai_contrast; }
*/
}
