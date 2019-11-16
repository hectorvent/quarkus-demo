package com.github.hectorvent.quarkusdemo.speakerapi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 *
 * @author Héctor Ventura <hectorvent@gmail.com>
 */
@Entity
@Table(name = "speaker")
public class Speaker extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(name = "twitter_account")
    public String twitterAccount;

    @Column(name = "github_account", length = 255)
    public String githubAccount;

    public static Speaker findByGitAccount(String githubAccount) {
        return find("github_account", githubAccount).firstResult();
    }
}
