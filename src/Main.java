import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import com.formdev.flatlaf.FlatDarkLaf;

public class Main extends JFrame {
    private JTextField nomeField, emailField, idField;
    private JPasswordField senhaField;
    private JTextArea outputArea;

    // Configurações do banco
    private static final String URL = "jdbc:mysql://localhost:3306/bd_dayone?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "maximus2011@MF";

    public Main() {
        // Configuração da janela principal
        setTitle("CRUD Java com Swing");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior para entradas
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID (para atualizar/deletar):"));
        idField = new JTextField();
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        inputPanel.add(nomeField);
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        inputPanel.add(senhaField);
        add(inputPanel, BorderLayout.NORTH);

        // Área de saída
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Botões para operações
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        JButton loginButton = new JButton("Login");
        JButton inserirButton = new JButton("Inserir");
        JButton listarButton = new JButton("Listar");
        JButton atualizarButton = new JButton("Atualizar");
        JButton deletarButton = new JButton("Deletar");

        buttonPanel.add(loginButton);
        buttonPanel.add(inserirButton);
        buttonPanel.add(listarButton);
        buttonPanel.add(atualizarButton);
        buttonPanel.add(deletarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ações dos botões
        loginButton.addActionListener(e -> abrirDashboard());
        inserirButton.addActionListener(e -> inserirUsuario());
        listarButton.addActionListener(e -> listarUsuarios());
        atualizarButton.addActionListener(e -> atualizarUsuario());
        deletarButton.addActionListener(e -> deletarUsuario());

        setVisible(true);
    }

    // Métodos CRUD
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void inserirUsuario() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Email e Senha são obrigatórios!");
            return;
        }

        String sql = "INSERT INTO tab_usuario (nome, senha, email) VALUES (?, ?, ?)";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, senha);
            stmt.setString(3, email);
            int linhasAfetadas = stmt.executeUpdate();
            outputArea.append("Usuário inserido com sucesso! Linhas afetadas: " + linhasAfetadas + "\n");
        } catch (SQLException e) {
            outputArea.append("Erro ao inserir usuário: " + e.getMessage() + "\n");
        }
    }

    private void listarUsuarios() {
        String sql = "SELECT * FROM tab_usuario";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            outputArea.setText("--- Lista de Usuários ---\n");
            while (rs.next()) {
                outputArea.append("ID: " + rs.getInt("id") + ", Nome: " + rs.getString("nome") +
                        ", Email: " + rs.getString("email") + "\n");
            }
        } catch (SQLException e) {
            outputArea.append("Erro ao listar usuários: " + e.getMessage() + "\n");
        }
    }

    private void atualizarUsuario() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID é obrigatório para atualizar!");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido!");
            return;
        }

        String nome = nomeField.getText();
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Email e Senha são obrigatórios!");
            return;
        }

        String sql = "UPDATE tab_usuario SET nome = ?, senha = ?, email = ? WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, senha);
            stmt.setString(3, email);
            stmt.setInt(4, id);
            int linhasAfetadas = stmt.executeUpdate();
            outputArea.append("Usuário atualizado com sucesso! Linhas afetadas: " + linhasAfetadas + "\n");
        } catch (SQLException e) {
            outputArea.append("Erro ao atualizar usuário: " + e.getMessage() + "\n");
        }
    }

    private void deletarUsuario() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID é obrigatório para deletar!");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido!");
            return;
        }

        String sql = "DELETE FROM tab_usuario WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            outputArea.append("Usuário deletado com sucesso! Linhas afetadas: " + linhasAfetadas + "\n");
        } catch (SQLException e) {
            outputArea.append("Erro ao deletar usuário: " + e.getMessage() + "\n");
        }
    }

    private void abrirDashboard() {
        JFrame dashboardFrame = new JFrame("Login");
        dashboardFrame.setSize(400, 300);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.setLayout(new FlowLayout());

        JLabel userLabel = new JLabel("Usuário:");
        JTextField t1 = new JTextField();
        t1.setPreferredSize(new Dimension(300, 30));

        JLabel passLabel = new JLabel("Senha:");
        JPasswordField t2 = new JPasswordField();
        t2.setPreferredSize(new Dimension(300, 30));

        JButton b1 = new JButton("Login");

        // Adicionando placeholders
        PlaceHolderUtil.addPlaceholder(t1, "Digite seu nome");
        PlaceHolderUtil.addPlaceholder(t2, "Digite sua senha");

        b1.addActionListener(e -> {
            String username = t1.getText();
            String password = new String(t2.getPassword());

            if (username.equals("matheus") && password.equals("123")) {
                JOptionPane.showMessageDialog(
                        dashboardFrame,
                        "Bem-vindo, " + username + "!",
                        "Login Bem-sucedido",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dashboardFrame.dispose();
                abrirNovoDashboard();
            } else {
                JOptionPane.showMessageDialog(
                        dashboardFrame,
                        "Usuário ou senha inválidos!",
                        "Erro de Login",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dashboardFrame.add(userLabel);
        dashboardFrame.add(t1);
        dashboardFrame.add(passLabel);
        dashboardFrame.add(t2);
        dashboardFrame.add(b1);

        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setVisible(true);
    }

    private void abrirNovoDashboard() {
        JFrame novoFrame = new JFrame("Dashboard");
        novoFrame.setSize(400, 300);
        novoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        novoFrame.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bem-vindo ao Dashboard!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        novoFrame.add(welcomeLabel, BorderLayout.CENTER);

        novoFrame.setLocationRelativeTo(null);
        novoFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.out.println("Não foi possível aplicar o tema escuro.");
        }

        SwingUtilities.invokeLater(Main::new);
    }
}
