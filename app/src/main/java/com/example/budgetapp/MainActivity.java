package com.example.budgetapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    // Chi tiêu
    private LinearLayout categoryLayout;
    private TextView tvTotalAmount, tvAvgPerDay;
    private ImageButton btnUnlock;
    private Button btnAdd;

    // Thu nhập
    private LinearLayout incomeLayout;
    private TextView tvTotalIncome, tvIncomePerDay;
    private Button btnAddIncome;

    private boolean isUnlocked = false;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Category> incomeList = new ArrayList<>();

    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chi tiêu
        categoryLayout = findViewById(R.id.categoryLayout);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvAvgPerDay = findViewById(R.id.tvAvgPerDay);
        btnUnlock = findViewById(R.id.btnUnlock);
        btnAdd = findViewById(R.id.btnAdd);

        // Thu nhập
        incomeLayout = findViewById(R.id.incomeLayout);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvIncomePerDay = findViewById(R.id.tvIncomePerDay);
        btnAddIncome = findViewById(R.id.btnAddIncome);

        btnUnlock.setOnClickListener(v -> {
            isUnlocked = !isUnlocked;
            btnUnlock.setImageResource(isUnlocked ?
                    android.R.drawable.ic_lock_idle_lock :
                    android.R.drawable.ic_lock_lock);
            updateCategoryUI();
            updateIncomeUI();
        });

        btnAdd.setOnClickListener(v -> showCategoryDialog(null, -1));
        btnAddIncome.setOnClickListener(v -> showIncomeDialog(null, -1));

        // Danh mục chi tiêu mẫu
        String[] expenseDefaults = {"Ăn tiệm", "Sinh hoạt", "Đi lại", "Trang phục", "Hưởng thụ", "Con cái", "Hiếu hỉ", "Nhà cửa"};
        for (String name : expenseDefaults) {
            categoryList.add(new Category(name, 0));
        }

        // Danh mục thu nhập mẫu
        String[] incomeDefaults = {"Lương", "Thưởng", "Đầu tư", "Khác"};
        for (String name : incomeDefaults) {
            incomeList.add(new Category(name, 0));
        }

        updateCategoryUI();
        updateIncomeUI();
    }

    private void updateCategoryUI() {
        categoryLayout.removeAllViews();
        int total = 0;

        for (int i = 0; i < categoryList.size(); i++) {
            Category c = categoryList.get(i);
            View item = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
            TextView text1 = item.findViewById(android.R.id.text1);
            TextView text2 = item.findViewById(android.R.id.text2);

            text1.setText(c.name);
            text2.setText(formatter.format(c.amount));

            int finalI = i;
            item.setOnClickListener(view -> {
                if (isUnlocked) showCategoryDialog(c, finalI);
            });

            item.setOnLongClickListener(view -> {
                if (isUnlocked) {
                    new AlertDialog.Builder(this)
                            .setTitle("Xóa danh mục")
                            .setMessage("Bạn có chắc muốn xóa '" + c.name + "' không?")
                            .setPositiveButton("Xóa", (d, w) -> {
                                categoryList.remove(finalI);
                                updateCategoryUI();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
                return true;
            });

            categoryLayout.addView(item);
            total += c.amount;
        }

        tvTotalAmount.setText(formatter.format(total));
        tvAvgPerDay.setText(formatter.format(total / 31.0));
    }

    private void updateIncomeUI() {
        incomeLayout.removeAllViews();
        int total = 0;

        for (int i = 0; i < incomeList.size(); i++) {
            Category c = incomeList.get(i);
            View item = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
            TextView text1 = item.findViewById(android.R.id.text1);
            TextView text2 = item.findViewById(android.R.id.text2);

            text1.setText(c.name);
            text2.setText(formatter.format(c.amount));

            int finalI = i;
            item.setOnClickListener(view -> {
                if (isUnlocked) showIncomeDialog(c, finalI);
            });

            item.setOnLongClickListener(view -> {
                if (isUnlocked) {
                    new AlertDialog.Builder(this)
                            .setTitle("Xóa thu nhập")
                            .setMessage("Bạn có chắc muốn xóa '" + c.name + "' không?")
                            .setPositiveButton("Xóa", (d, w) -> {
                                incomeList.remove(finalI);
                                updateIncomeUI();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
                return true;
            });

            incomeLayout.addView(item);
            total += c.amount;
        }

        tvTotalIncome.setText(formatter.format(total));
        tvIncomePerDay.setText(formatter.format(total / 31.0));
    }

    private void showCategoryDialog(Category existing, int index) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText edtName = dialogView.findViewById(R.id.edtCategoryName);
        EditText edtAmount = dialogView.findViewById(R.id.edtCategoryAmount);

        if (existing != null) {
            edtName.setText(existing.name);
            edtAmount.setText(String.valueOf(existing.amount));
        }

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Thêm danh mục" : "Chỉnh sửa danh mục")
                .setView(dialogView)
                .setPositiveButton("Lưu", (d, w) -> {
                    String name = edtName.getText().toString();
                    int amount = 0;
                    try {
                        amount = Integer.parseInt(edtAmount.getText().toString());
                    } catch (NumberFormatException ignored) {}

                    if (existing == null) {
                        categoryList.add(new Category(name, amount));
                    } else {
                        existing.name = name;
                        existing.amount = amount;
                        categoryList.set(index, existing);
                    }

                    updateCategoryUI();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showIncomeDialog(Category existing, int index) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText edtName = dialogView.findViewById(R.id.edtCategoryName);
        EditText edtAmount = dialogView.findViewById(R.id.edtCategoryAmount);

        if (existing != null) {
            edtName.setText(existing.name);
            edtAmount.setText(String.valueOf(existing.amount));
        }

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Thêm thu nhập" : "Chỉnh sửa thu nhập")
                .setView(dialogView)
                .setPositiveButton("Lưu", (d, w) -> {
                    String name = edtName.getText().toString();
                    int amount = 0;
                    try {
                        amount = Integer.parseInt(edtAmount.getText().toString());
                    } catch (NumberFormatException ignored) {}

                    if (existing == null) {
                        incomeList.add(new Category(name, amount));
                    } else {
                        existing.name = name;
                        existing.amount = amount;
                        incomeList.set(index, existing);
                    }

                    updateIncomeUI();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    static class Category {
        String name;
        int amount;

        public Category(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
