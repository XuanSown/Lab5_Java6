package com.example.Lab5.controller;

import com.example.Lab5.entity.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StudentManagerUI extends JFrame {

    private JTextField txtId, txtName, txtMark;
    private JRadioButton rdoMale, rdoFemale;
    private JTable tblStudents;
    private DefaultTableModel tableModel;

    private final String API_URL = "http://localhost:8080/students";
    private final tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();

    public StudentManagerUI() {
        setTitle("Quản lý sinh viên");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();

        // Khởi đầu: Tải và hiển thị danh sách sinh viên lên bảng
        loadTableData();
    }

    private void initComponents() {
        // --- PHẦN FORM NHẬP LIỆU ---
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlForm.add(new JLabel("Id"));
        txtId = new JTextField();
        pnlForm.add(txtId);

        pnlForm.add(new JLabel("Full Name"));
        txtName = new JTextField();
        pnlForm.add(txtName);

        pnlForm.add(new JLabel("Gender"));
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rdoMale = new JRadioButton("Male");
        rdoFemale = new JRadioButton("Female", true);
        ButtonGroup bgGender = new ButtonGroup();
        bgGender.add(rdoMale);
        bgGender.add(rdoFemale);
        pnlGender.add(rdoMale);
        pnlGender.add(rdoFemale);
        pnlForm.add(pnlGender);

        pnlForm.add(new JLabel("Average Mark"));
        txtMark = new JTextField();
        pnlForm.add(txtMark);

        // --- PHẦN NÚT CHỨC NĂNG ---
        JPanel pnlButtons = new JPanel(new FlowLayout());
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnReset = new JButton("Reset");

        pnlButtons.add(btnCreate);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnReset);

        // --- PHẦN BẢNG DỮ LIỆU ---
        String[] columns = {"Id", "Full Name", "Gender", "Mark"};
        tableModel = new DefaultTableModel(columns, 0);
        tblStudents = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblStudents);

        // Bố cục tổng thể
        setLayout(new BorderLayout());
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- XỬ LÝ SỰ KIỆN (EVENTS) ---

        // Edit.DoubleClick: Tải và hiển thị sinh viên lên form
        tblStudents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblStudents.getSelectedRow();
                    if (row >= 0) {
                        txtId.setText(tblStudents.getValueAt(row, 0).toString());
                        txtName.setText(tblStudents.getValueAt(row, 1).toString());
                        boolean isMale = tblStudents.getValueAt(row, 2).toString().equals("true");
                        rdoMale.setSelected(isMale);
                        rdoFemale.setSelected(!isMale);
                        txtMark.setText(tblStudents.getValueAt(row, 3).toString());
                    }
                }
            }
        });

        // Create.Click: Thêm mới -> Xóa trắng -> Tải lại bảng
        btnCreate.addActionListener(e -> {
            try {
                Student student = getStudentFromForm();
                sendApiRequest("POST", API_URL, mapper.writeValueAsBytes(student));
                resetForm();
                loadTableData();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Update.Click: Cập nhật -> Tải lại bảng
        btnUpdate.addActionListener(e -> {
            try {
                Student student = getStudentFromForm();
                sendApiRequest("PUT", API_URL + "/" + student.getId(), mapper.writeValueAsBytes(student));
                loadTableData();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Delete.Click: Xóa -> Xóa trắng -> Tải lại bảng
        btnDelete.addActionListener(e -> {
            try {
                String id = txtId.getText();
                sendApiRequest("DELETE", API_URL + "/" + id, null);
                resetForm();
                loadTableData();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Reset.Click: Xóa trắng form
        btnReset.addActionListener(e -> resetForm());
    }

    // --- CÁC HÀM TIỆN ÍCH ---

    private Student getStudentFromForm() {
        return new Student(
                txtId.getText(),
                txtName.getText(),
                Double.parseDouble(txtMark.getText()),
                rdoMale.isSelected()
        );
    }

    private void resetForm() {
        txtId.setText("");
        txtName.setText("");
        txtMark.setText("");
        rdoFemale.setSelected(true);
    }

    // Gọi API lấy danh sách và đổ vào JTable
    private void loadTableData() {
        try {
            byte[] response = sendApiRequest("GET", API_URL, null);
            // API Spring Boot Bài 4 trả về mảng/List JSON (không phải Map như Firebase)
            List<Student> list = mapper.readValue(response, new tools.jackson.core.type.TypeReference<List<Student>>() {});

            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            for (Student s : list) {
                tableModel.addRow(new Object[]{s.getId(), s.getName(), s.isGender(), s.getMark()});
            }
        } catch (Exception e) {
            System.err.println("Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    // Hàm gọi HTTP chung (Dựa trên tài liệu hướng dẫn)
    private byte[] sendApiRequest(String method, String urlString, byte[] data) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");

        if (data != null && (method.equals("POST") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(data);
        }

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] block = new byte[4096];
            int n;
            while ((n = is.read(block)) > 0) {
                out.write(block, 0, n);
            }
            conn.disconnect();
            return out.toByteArray();
        } else {
            conn.disconnect();
            throw new Exception("HTTP Error Code: " + conn.getResponseCode());
        }
    }

    // Hàm Main để khởi chạy UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentManagerUI().setVisible(true);
        });
    }
}