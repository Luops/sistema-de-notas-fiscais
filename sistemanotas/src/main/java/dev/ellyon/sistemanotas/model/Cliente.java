package dev.ellyon.sistemanotas.model;

import dev.ellyon.sistemanotas.model.enums.TipoPessoa;
import dev.ellyon.sistemanotas.security.CpfCnpjEncryptor;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cliente")
@Data
public class Cliente extends Entidade{
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 50)
    private TipoPessoa tipoPessoa;

    @Convert(converter = CpfCnpjEncryptor.class)
    @Column(name = "cpf_cnpj", nullable = false, length = 20, unique = true)
    private String cpfCnpj;

    @Column(name = "cpf_cnpj_hash", unique = true)
    private String cpfCnpjHash;

    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "endereco_completo", nullable = false, length = 500)
    private String enderecoCompleto;

    @Column(name = "numero_endereco", nullable = false, length = 500)
    private String numeroEndereco;

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 100)
    private String estadoUF;

    @Column(name = "cep", nullable = false, length = 20)
    private String cep;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "is_ativo", nullable = false)
    private Boolean isAtivo;

    // Construtor padrão
    public Cliente(){
        super();
    }

    // Construtor com todos atributos
    public Cliente(Long id, String nome, TipoPessoa tipoPessoa, String cpfCnpj, String inscricaoEstadual,
                   String email, String telefone, String enderecoCompleto, String numeroEndereco, String cidade,
                   String estadoUF, String cep, String bairro, Empresa empresa, Boolean isAtivo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.email = email;
        this.telefone = telefone;
        this.enderecoCompleto = enderecoCompleto;
        this.numeroEndereco = numeroEndereco;
        this.cidade = cidade;
        this.estadoUF = estadoUF;
        this.cep = cep;
        this.bairro = bairro;
        this.empresa = empresa;
        this.isAtivo = isAtivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor sem id e timestamps
    public Cliente(String nome, TipoPessoa tipoPessoa, String cpfCnpj, String inscricaoEstadual,
                   String email, String telefone, String enderecoCompleto, String cidade,
                   String estadoUF, String cep, String bairro, Boolean isAtivo, Empresa empresa) {
        this.nome = nome;
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.email = email;
        this.telefone = telefone;
        this.enderecoCompleto = enderecoCompleto;
        this.cidade = cidade;
        this.estadoUF = estadoUF;
        this.cep = cep;
        this.bairro = bairro;
        this.isAtivo = isAtivo;
        this.empresa = empresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
        // Gerar hash para indexação
        this.cpfCnpjHash = DigestUtils.sha256Hex(cpfCnpj);
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getCpfCnpjHash() {
        return cpfCnpjHash;
    }

    public void setCpfCnpjHash(String cpfCnpjHash) {
        this.cpfCnpjHash = cpfCnpjHash;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstadoUF() {
        return estadoUF;
    }

    public void setEstadoUF(String estadoUF) {
        this.estadoUF = estadoUF;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Boolean getIsAtivo() {
        return isAtivo;
    }

    public void setIsAtivo(Boolean ativo) {
        isAtivo = ativo;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", tipoPessoa=" + tipoPessoa +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", inscricaoEstadual='" + inscricaoEstadual + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", enderecoCompleto='" + enderecoCompleto + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estadoUF='" + estadoUF + '\'' +
                ", cep='" + cep + '\'' +
                ", bairro='" + bairro + '\'' +
                ", empresa=" + empresa +
                ", isAtivo=" + isAtivo +
                '}';
    }
}
